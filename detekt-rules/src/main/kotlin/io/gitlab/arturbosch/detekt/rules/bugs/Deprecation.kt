package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Deprecated elements are expected to be removed in future. Alternatives should be found if possible.
 */
class Deprecation(config: Config) : Rule(config) {

    override val issue = Issue(
        "Deprecation",
        Severity.Defect,
        "Deprecated elements should not be used.",
        Debt.TWENTY_MINS
    )

    override fun visitElement(element: PsiElement) {
        if (bindingContext == BindingContext.EMPTY) return
        if (bindingContext.diagnostics.forElement(element).firstOrNull { it.factory == Errors.DEPRECATION } != null) {
            report(CodeSmell(issue, Entity.from(element), "$element is deprecated."))
        }
        super.visitElement(element)
    }
}
