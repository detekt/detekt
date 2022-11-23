package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.valuesWithReason
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

/**
 * This rule allows to set a list of forbidden annotations. This can be used to discourage the use
 * of language annotations which do not require explicit import.
 *
 * <noncompliant>
 * @@SuppressWarnings("unused")
 * class SomeClass()
 * </noncompliant>
 *
 */
@RequiresTypeResolution
class ForbiddenAnnotation(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Avoid using this annotation.",
        Debt.FIVE_MINS
    )

    @Configuration(
        "List of fully qualified annotation classes which are forbidden. " +
            "For example, `kotlin.jvm.Transient`."
    )
    private val annotations: Map<String, Forbidden> by config(
        valuesWithReason(
            "java.lang.SuppressWarnings" to "it is a java annotation. Use `Suppress` instead.",
            "java.lang.Deprecated" to "it is a java annotation. Use `kotlin.Deprecated` instead.",
            "java.lang.annotation.Documented" to "it is a java annotation. Use `kotlin.annotation.MustBeDocumented` instead.",
            "java.lang.annotation.Target" to "it is a java annotation. Use `kotlin.annotation.Target` instead.",
            "java.lang.annotation.Retention" to "it is a java annotation. Use `kotlin.annotation.Retention` instead.",
            "java.lang.annotation.Repeatable" to "it is a java annotation. Use `kotlin.annotation.Repeatable` instead.",
            "java.lang.annotation.Inherited" to "Kotlin doesn't support @Inherited annotation, see https://youtrack.jetbrains.com/issue/KT-22265",
        )
    ) { list ->
        list.associate { it.value to Forbidden(it.value, it.reason) }
    }

    override fun visitAnnotationEntry(annotation: KtAnnotationEntry) {
        super.visitAnnotationEntry(annotation)
        if (annotations.isEmpty()) {
            return
        }

        annotation.typeReference?.fqNameOrNull()?.let {
            check(annotation, it)
        }
    }

    override fun visitExpression(expression: KtExpression) {
        super.visitExpression(expression)

        if (annotations.isEmpty()) {
            return
        }
        expression.expressionTypeOrNull()?.fqNameOrNull()?.let {
            check(expression, it)
        }
    }

    private fun check(element: KtElement, fqName: FqName) {
        val forbidden = annotations[fqName.asString()]

        if (forbidden != null) {
            val message = if (forbidden.reason != null) {
                "The annotation `${forbidden.name}` has been forbidden: ${forbidden.reason}"
            } else {
                "The annotation `${forbidden.name}` has been forbidden in the detekt config."
            }
            val location = Location.from(element).let { location ->
                location.copy(
                    text = location.text.copy(
                        end = element.children.firstOrNull()?.endOffset ?: location.text.end
                    )
                )
            }
            report(CodeSmell(issue, Entity.from(element, location), message))
        }
    }

    private data class Forbidden(val name: String, val reason: String?)

    private fun KtTypeReference.fqNameOrNull(): FqName? {
        return if (bindingContext != BindingContext.EMPTY) {
            bindingContext[BindingContext.TYPE, this]?.fqNameOrNull()
        } else {
            null
        }
    }

    private fun KtExpression.expressionTypeOrNull(): KotlinType? {
        return if (bindingContext != BindingContext.EMPTY) {
            bindingContext[BindingContext.EXPRESSION_TYPE_INFO, this]?.type
        } else {
            null
        }
    }
}
