package com.example.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

class AutoBindingProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var hasGenerated = false

    /**
     * process 函数只会编译一次，并不是每次编译时都会执行；
     * 后续只有开发者修改该函数时，才会重新编译一次；
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("======================>>> AutoBindingProcessor 开始编译")
        if (!hasGenerated) {
            hasGenerated = true
            generate()
        }

        return emptyList()
    }

    private fun generate() {
        val code = """
            package com.example.green_agriculture.base

            import android.content.Context
            import android.view.LayoutInflater
            import com.example.annotation.AutoBinding
            
            /**
             * AutoBinding 注解处理器
             */
            object AutoBindingProcessor {
                fun bind(instance: BaseFragment, context: Context) {
                    val clazz = instance.javaClass

                    for (field in clazz.declaredFields) {
                        if (field.isAnnotationPresent(AutoBinding::class.java)) {
                            val bindingClassName = "${'$'}{field.type.packageName}.${'$'}{field.type.simpleName}"
                            val bindingClass = Class.forName(bindingClassName)
                            val inflateMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java)
                            val binding = inflateMethod.invoke(null, LayoutInflater.from(context))
                            field.isAccessible = true
                            field.set(instance, binding)
                        }
                    }
                }
            }
        """.trimIndent()

        codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = "com.example.green_agriculture",
            fileName = "AutoBindingProcessor"
        ).use {
            it.write(code.toByteArray())
        }
    }
}

/**
 * KSP 服务提供者，告诉 KSP 这里有一个服务器
 * 在 SymbolProcessorProvider 文件中注册 AutoBindingProcessorProvider
 * 注册后，KSP 在编译阶段就会扫描 KSP 模块（该项目中的 processor 模块）中注册的服务，通过 create 函数生成代码。
 */
class AutoBindingProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoBindingProcessor(environment.codeGenerator, environment.logger)
    }
}