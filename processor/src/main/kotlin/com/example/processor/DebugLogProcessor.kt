package com.example.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate

class DebugLogProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) :
    SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.example.annotation.DebugLog")

        val validMethods = symbols
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { it.validate() }
            .toList()

        for (func in validMethods) {
            generateLoggedFunction(func)
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    private fun generateLoggedFunction(func: KSFunctionDeclaration) {
        val classDecl = func.parentDeclaration as? KSClassDeclaration ?: run {
            logger.error("DebugLog only supports member functions", func)
            return
        }
        // 通过 KSClassDeclaration，我们可以获得这个类的包名、简单类名、
        val packageName = classDecl.packageName.asString()
        val className = classDecl.simpleName.asString()
        val funcName = func.simpleName.asString()

        classDecl.annotations
        func.annotations

        // 2. 读取注解参数
        val annotation = func.annotations.find { it.shortName.asString() == "DebugLog" }
        val tag = (annotation?.arguments?.firstOrNull()?.value as? String) ?: "DEBUG"

        // 3. 安全构造参数声明（name: Type）
        val funcParamsDecl = func.parameters.joinToString(", ") { param ->
            val paramName = param.name?.asString() ?: "arg"
            val paramType =
                param.type.resolve().declaration.qualifiedName?.asString() ?: "kotlin.Any"
            "$paramName: $paramType"
        }

        // 4. 调用原函数时的参数值列表
        val funcParamsCall = func.parameters.joinToString(", ") { it.name?.asString() ?: "arg" }

        // 5. 返回类型（全限定名）
        val returnType =
            func.returnType?.resolve()?.declaration?.qualifiedName?.asString() ?: "kotlin.Unit"

        func.returnType?.resolve()?.declaration?.qualifiedName

        // 6. 新函数名
        val newFuncName = "${funcName}WithLog"

        // 7. 日志语句（全限定调用）
        val logStart = """android.util.Log.d("$tag", "$funcName start")"""
        val logEnd = """android.util.Log.d("$tag", "$funcName end")"""

        // 8. 处理函数调用与返回值
        val (callOriginal, returnStatement) = if (returnType == "kotlin.Unit") {
            "$funcName($funcParamsCall)" to ""
        } else {
            "val __result = $funcName($funcParamsCall)" to "\n    return __result"
        }

        val fileContent = """
            package $packageName
            
            fun $className.$newFuncName(): $returnType {
                $logStart
                $callOriginal
                $logEnd$returnStatement
            }
            """.trimIndent()

        codeGenerator.createNewFile(
            dependencies = Dependencies(true, func.containingFile!!),
            packageName = packageName,
            fileName = "${className}${funcName}Debug"
        ).use { stream ->
            stream.write(fileContent.toByteArray())
        }
    }
}

class DebugLogProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DebugLogProcessor(environment.codeGenerator, environment.logger)
    }
}