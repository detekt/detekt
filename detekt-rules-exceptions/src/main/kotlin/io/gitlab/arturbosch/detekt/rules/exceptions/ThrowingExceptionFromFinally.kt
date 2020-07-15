package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType

/**
 * This rule reports all cases where exceptions are thrown from a `finally` block. Throwing exceptions from a `finally`
 * block should be avoided as it can lead to confusion and discarded exceptions.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } finally {
 *         throw IOException()
 *     }
 * }
 * </noncompliant>
 */
class ThrowingExceptionFromFinally(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ThrowingExceptionFromFinally", Severity.Defect,
            "Do not throw an exception within a finally statement. This can discard exceptions and is confusing.",
            Debt.TWENTY_MINS)

    override fun visitFinallySection(finallySection: KtFinallySection) {
        finallySection.finalExpression.forEachDescendantOfType<KtThrowExpression> {
            report(CodeSmell(issue, Entity.from(it), issue.description))
        }
    }
}
