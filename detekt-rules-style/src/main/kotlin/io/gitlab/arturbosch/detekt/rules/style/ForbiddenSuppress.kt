package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

/**
 * Report suppressions of all forbidden rules.
 *
 * This rule allows to set a list of [rules] whose suppression is forbidden.
 * This can be used to discourage the abuse of the `Suppress` and `SuppressWarnings` annotations.
 *
 * This rule is not capable of reporting suppression of itself, as that's a language feature with precedence.
 *
 * <noncompliant>
 * package foo
 *
 * // When the rule "MaximumLineLength" is forbidden
 * @@Suppress("MaximumLineLength", "UNCHECKED_CAST")
 * class Bar
 * </noncompliant>
 *
 * <compliant>
 * package foo
 *
 * // When the rule "MaximumLineLength" is forbidden
 * @@Suppress("UNCHECKED_CAST")
 * class Bar
 * </compliant>
 */
class ForbiddenSuppress(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Suppressing a rule which is forbidden in current configuration.",
        Debt.TEN_MINS
    )

    @Configuration("Rules whose suppression is forbidden.")
    private val rules: List<String> by config(emptyList())

    override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
        if (rules.isEmpty()) return
        val shortName = annotationEntry.shortName?.asString()
        if (shortName == KOTLIN_SUPPRESS || shortName == JAVA_SUPPRESS) {
            val nonCompliantRules = annotationEntry.children
                .find { it is KtValueArgumentList }
                ?.children
                ?.filterIsInstance<KtValueArgument>()
                ?.filterForbiddenRules()
                .orEmpty()
            if (nonCompliantRules.isNotEmpty()) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(annotationEntry),
                        message = "Cannot @Suppress ${nonCompliantRules.formatMessage()} " +
                            "due to the current configuration.",
                    )
                )
            }
        }
    }

    private fun List<String>.formatMessage(): String = if (size > 1) {
        "rules "
    } else {
        "rule "
    } + joinToString(", ") { "\"$it\"" }

    private fun List<KtValueArgument>.filterForbiddenRules(): List<String> = mapNotNull { argument ->
        val text = argument.findDescendantOfType<KtLiteralStringTemplateEntry>()?.text
        if (rules.contains(text)) text else null
    }

    private companion object {
        private const val KOTLIN_SUPPRESS = "Suppress"
        private const val JAVA_SUPPRESS = "SuppressWarnings"
    }
}
