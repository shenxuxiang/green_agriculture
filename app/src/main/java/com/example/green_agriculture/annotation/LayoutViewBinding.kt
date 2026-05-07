package com.example.green_agriculture.annotation

//
//@Retention(AnnotationRetention.RUNTIME)
//@Target(AnnotationTarget.FIELD)
//annotation class LayoutViewBinding(val layoutName: String)
//
//object BindingProcessor {
//    fun processBinding(instance: Any, ctx: Context) {
//        val clazz = instance.javaClass
//
//        val fields = clazz.declaredFields
//
//        for (field in fields) {
//            if (field.isAnnotationPresent(LayoutViewBinding::class.java)) {
//                val bindingClassName = "${field.type.packageName}.${field.type.simpleName}"
//
//                val bindingClass = Class.forName(bindingClassName)
//
//                val inflateMethod = bindingClass.getMethod(
//                    "inflate",
//                    LayoutInflater::class.java,
//                )
//
//                val inflate = LayoutInflater.from(ctx)
//                val binding = inflateMethod.invoke(null, inflate)
//
//                field.isAccessible = true
//                field.set(instance, binding)
//            }
//        }
//    }
//}
//
//
//class LayoutViewBindingProcessor(
//    private val codeGenerator: CodeGenerator,
//    private val logger: KSPLogger,
//) : SymbolProcessor {
//
//    override fun process(resolver: Resolver): List<KSAnnotated> {
//        // 找到所有被 @LayoutViewBinding 注解的字段
//        val symbols = resolver.getSymbolsWithAnnotation("com.example.annotation.LayoutViewBinding")
//        val validFields = symbols
//            .filterIsInstance<KSPropertyDeclaration>()
//            .filter { it.validate() }
//            .toList()
//
//        // 按所属类分组
//        val classToFields = validFields.groupBy { it.parentDeclaration as KSClassDeclaration }
//
//        for ((classDecl, fields) in classToFields) {
//            generateBindingHelper(classDecl, fields)
//        }
//
//        return symbols.filterNot { it.validate() }.toList()
//    }
//
//    private fun generateBindingHelper(
//        classDecl: KSClassDeclaration,
//        fields: List<KSPropertyDeclaration>,
//    ) {
//        val packageName = classDecl.packageName.asString()
//        val className = classDecl.simpleName.asString()
//        val helperClassName = "${className}BindingHelper"
//
//        // 构建 bind 方法体
//        val bindStatements = fields.joinToString("\n        ") { field ->
//            val fieldName = field.simpleName.asString()
//            val bindingType = field.type.resolve().declaration.qualifiedName?.asString() ?: return
//            // 生成：target.fieldName = BindingClass.inflate(LayoutInflater.from(target))
//            "target.$fieldName = $bindingType.inflate(android.view.LayoutInflater.from(target))"
//        }
//
//        val fileContent = """
//            package $packageName
//
//            import android.content.Context
//            import android.view.LayoutInflater
//
//            object $helperClassName {
//                fun bind(target: android.content.Context) {
//                    ${bindStatements}
//                }
//            }
//        """.trimIndent()
//
//        // 写入生成文件
//        codeGenerator.createNewFile(
//            dependencies = Dependencies(true, classDecl.containingFile!!),
//            packageName = packageName,
//            fileName = helperClassName
//        ).use { stream ->
//            stream.write(fileContent.toByteArray())
//        }
//    }
//}
//
//// 处理器提供者
//class LayoutViewBindingProcessorProvider : SymbolProcessorProvider {
//    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
//        return LayoutViewBindingProcessor(environment.codeGenerator, environment.logger)
//    }
//}