// processor/src/main/kotlin/com/example/processor/LayoutViewBindingProcessor.kt
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
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate

class LayoutViewBindingProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.example.annotation.LayoutViewBinding")
        val validFields = symbols
            .filterIsInstance<KSPropertyDeclaration>()
            .filter { it.validate() }
            .toList()

        val classToFields = validFields.groupBy { it.parentDeclaration as KSClassDeclaration }

        for ((classDecl, fields) in classToFields) {
            generateBindingHelper(classDecl, fields)
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    private fun generateBindingHelper(
        classDecl: KSClassDeclaration,
        fields: List<KSPropertyDeclaration>,
    ) {
        val packageName = classDecl.packageName.asString()
        val className = classDecl.simpleName.asString()
        val helperClassName = "${className}BindingHelper"

        val bindStatements = fields.joinToString("\n        ") { field ->
            val fieldName = field.simpleName.asString()
            field.annotations
            val bindingType = field.type.resolve().declaration.qualifiedName!!.asString()
            """
                val inflate = android.view.LayoutInflater.from(context) 
                val field = target.javaClass.getDeclaredField("$fieldName") 
                field.isAccessible = true
                field.set(target, $bindingType.inflate(inflate))
            """.trimIndent()
        }

        val fileContent = """
            package $packageName

            object $helperClassName {
                fun bind(target: $className, context: android.content.Context) {
                    ${bindStatements}
                }
            }
        """.trimIndent()

        codeGenerator.createNewFile(
            dependencies = Dependencies(true, classDecl.containingFile!!),
            packageName = packageName,
            fileName = helperClassName
        ).use { stream ->
            stream.write(fileContent.toByteArray())
        }
    }
}


class LayoutViewBindingProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LayoutViewBindingProcessor(environment.codeGenerator, environment.logger)
    }
}