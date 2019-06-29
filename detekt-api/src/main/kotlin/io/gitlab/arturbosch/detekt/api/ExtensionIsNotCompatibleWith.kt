package io.gitlab.arturbosch.detekt.api

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.reflect.KClass

@Target(CLASS)
@Inherited
annotation class ExtensionIsNotCompatibleWith(vararg val value: KClass<out Extension>)

val Class<out Extension>.notCompatibleClasses: List<Class<out Extension>>
    get() = getAnnotation(ExtensionIsNotCompatibleWith::class.java)
        ?.value
        ?.map { it.java }
        ?: emptyList()
