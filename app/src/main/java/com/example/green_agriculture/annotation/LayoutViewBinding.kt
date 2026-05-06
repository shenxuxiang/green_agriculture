package com.example.green_agriculture.annotation

import android.content.Context
import android.view.LayoutInflater

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class LayoutViewBinding(val layoutName: String)

object BindingProcessor {
    fun processBinding(instance: Any, ctx: Context) {
        val clazz = instance.javaClass

        val fields = clazz.declaredFields

        for (field in fields) {
            if (field.isAnnotationPresent(LayoutViewBinding::class.java)) {
                val bindingClassName = "${field.type.packageName}.${field.type.simpleName}"

                val bindingClass = Class.forName(bindingClassName)

                val inflateMethod = bindingClass.getMethod(
                    "inflate",
                    LayoutInflater::class.java,
                )

                val inflate = LayoutInflater.from(ctx)
                val binding = inflateMethod.invoke(null, inflate)

                field.isAccessible = true
                field.set(instance, binding)
            }
        }
    }
}