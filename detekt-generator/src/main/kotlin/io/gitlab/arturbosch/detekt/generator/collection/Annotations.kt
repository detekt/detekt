package io.gitlab.arturbosch.detekt.generator.collection

import org.jetbrains.kotlin.psi.KtClassOrObject
import kotlin.reflect.KClass

fun KtClassOrObject.isAnnotatedWith(annotation: KClass<out Annotation>) =
    annotationEntries.any { it.shortName?.identifier == annotation.simpleName }
