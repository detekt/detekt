package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Deprecated elements are expected to be removed in the future. Alternatives should be found if possible.
 *
 */
@Alias("DEPRECATION")
class Deprecation(config: Config) :
    Rule(
        config,
        "Deprecated elements should not be used."
    ),
    RequiresFullAnalysis {
    override lateinit var bindingContext: BindingContext

    override fun visitElement(element: PsiElement) {
        val diagnostic = hasDeprecationCompilerWarnings(element)
        if (diagnostic != null) {
            val entity = if (element is KtNamedDeclaration) Entity.atName(element) else Entity.from(element)
            report(CodeSmell(entity, """${element.text} is deprecated with message "${diagnostic.b}""""))
        }
        super.visitElement(element)
    }

    private fun hasDeprecationCompilerWarnings(element: PsiElement) =
        bindingContext.diagnostics
            .forElement(element)
            .firstOrNull { it.factory == Errors.DEPRECATION }
            ?.let { Errors.DEPRECATION.cast(it) }
}
