package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.TextLocation
import dev.detekt.api.ValueWithReason
import dev.detekt.api.config
import dev.detekt.api.valuesWithReason
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset

/**
 * This rule allows to set a list of forbidden annotations. This can be used to discourage the use
 * of language annotations which do not require explicit import.
 *
 * <noncompliant>
 * @@SuppressWarnings("unused")
 * class SomeClass()
 * </noncompliant>
 *
 * <compliant>
 * @@Suppress("unused")
 * class SomeClass()
 * </compliant>
 */
class ForbiddenAnnotation(config: Config) :
    Rule(
        config,
        "Avoid using this annotation."
    ),
    RequiresAnalysisApi {

    @Configuration(
        "List of fully qualified annotation classes which are forbidden."
    )
    private val annotations: Map<String, ValueWithReason> by config(
        valuesWithReason(
            "java.lang.SuppressWarnings" to "it is a java annotation. Use `Suppress` instead.",
            "java.lang.Deprecated" to "it is a java annotation. Use `kotlin.Deprecated` instead.",
            "java.lang.annotation.Documented" to "it is a java annotation. " +
                "Use `kotlin.annotation.MustBeDocumented` instead.",
            "java.lang.annotation.Target" to "it is a java annotation. Use `kotlin.annotation.Target` instead.",
            "java.lang.annotation.Retention" to "it is a java annotation. Use `kotlin.annotation.Retention` instead.",
            "java.lang.annotation.Repeatable" to "it is a java annotation. Use `kotlin.annotation.Repeatable` instead.",
            "java.lang.annotation.Inherited" to "Kotlin does not support @Inherited annotation, " +
                "see https://youtrack.jetbrains.com/issue/KT-22265",
        )
    ) { list ->
        list.associateBy { it.value }
    }

    override fun visitAnnotationEntry(annotation: KtAnnotationEntry) {
        super.visitAnnotationEntry(annotation)

        analyze(annotation) {
            check(annotation, annotation.typeReference?.type)
        }
    }

    override fun visitExpression(expression: KtExpression) {
        super.visitExpression(expression)

        analyze(expression) {
            check(expression, expression.expressionType)
        }
    }

    private fun check(element: KtElement, type: KaType?) {
        val forbidden = annotations[type?.symbol?.classId?.asSingleFqName()?.asString()]

        if (forbidden != null) {
            val message = if (forbidden.reason != null) {
                "The annotation `${forbidden.value}` has been forbidden: ${forbidden.reason}"
            } else {
                "The annotation `${forbidden.value}` has been forbidden in the detekt config."
            }
            val location = Location.from(element).let { location ->
                Location(
                    location.source,
                    location.endSource,
                    TextLocation(location.text.start, element.children.firstOrNull()?.endOffset ?: location.text.end),
                    location.path
                )
            }
            report(Finding(Entity.from(element, location), message))
        }
    }
}
