package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtAnnotationEntry

/**
 * This rule detects annotations which use the arrayOf(...) syntax instead of the array literal [...] syntax.
 * The latter should be preferred as it is more readable.
 *
 * <noncompliant>
 * @@PositiveCase(arrayOf("..."))
 * </noncompliant>
 *
 * <compliant>
 * @@NegativeCase(["..."])
 * </compliant>
 */
class UseArrayLiteralsInAnnotations(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Array literals '[...]' should be preferred as they are more readable than 'arrayOf(...)' expressions.",
        Debt.FIVE_MINS
    )

    override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
        for (argument in annotationEntry.valueArguments) {
            val expr = argument.getArgumentExpression()?.text ?: continue
            if (expr.startsWith(LONG_ANNOTATION_LITERAL_FORM)) {
                report(CodeSmell(issue, Entity.from(argument.asElement()), issue.description))
            }
        }
    }

    companion object {
        const val LONG_ANNOTATION_LITERAL_FORM = "arrayOf("
    }
}
