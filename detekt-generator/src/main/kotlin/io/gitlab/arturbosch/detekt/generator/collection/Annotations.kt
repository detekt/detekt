package io.gitlab.arturbosch.detekt.generator.collection

import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject
import kotlin.reflect.KClass

fun KtClassOrObject.isAnnotatedWith(annotation: KClass<out Annotation>): Boolean =
    annotationEntries.any { it.isOfType(annotation) }

fun KtClassOrObject.firstAnnotationParameterOrNull(annotation: KClass<out Annotation>): String? =
    annotationEntries
        .firstOrNull { it.isOfType(annotation) }
        ?.firstParameterOrNull()

private fun KtAnnotationEntry.isOfType(annotation: KClass<out Annotation>) =
    shortName?.identifier == annotation.simpleName

private fun KtAnnotationEntry.firstParameterOrNull() =
    valueArguments
        .firstOrNull()
        ?.getArgumentExpression()
        ?.text
        ?.withoutQuotes()

private fun String.withoutQuotes() = removePrefix(QUOTES).removeSuffix(QUOTES)

private const val QUOTES = "\""
