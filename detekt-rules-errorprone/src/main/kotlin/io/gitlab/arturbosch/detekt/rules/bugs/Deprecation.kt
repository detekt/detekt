package io.gitlab.arturbosch.detekt.rules.bugs

import com.intellij.psi.PsiElement
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

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

    @Configuration("Ignore deprecation in import statements")
    private val excludeImportStatements: Boolean by config(false)

    override fun visitElement(element: PsiElement) {
        val diagnostic = hasDeprecationCompilerWarnings(element)
        if (diagnostic != null) {
            val entity =
                if (element is KtNamedDeclaration) Entity.atName(element) else Entity.from(element)
            if (shouldIgnoreThisElement(element)) {
                // not reporting for import statements
                report(
                    Finding(
                        entity,
                        """${element.text} is deprecated with message "${diagnostic.b}""""
                    )
                )
            }
        }
        super.visitElement(element)
    }

    private fun shouldIgnoreThisElement(element: PsiElement) =
        !(excludeImportStatements && element.getParentOfType<KtImportDirective>(true) != null)

    private fun hasDeprecationCompilerWarnings(element: PsiElement) =
        bindingContext.diagnostics
            .forElement(element)
            .firstOrNull { it.factory == Errors.DEPRECATION }
            ?.let { Errors.DEPRECATION.cast(it) }
}
