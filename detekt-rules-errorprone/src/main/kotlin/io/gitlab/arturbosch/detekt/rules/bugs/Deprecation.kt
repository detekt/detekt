package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaFirDiagnostic
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNamedDeclaration

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
    RequiresAnalysisApi {

    @Configuration("Ignore deprecation in import statements")
    private val excludeImportStatements: Boolean by config(false)

    override fun visitKtElement(element: KtElement) {
        if (excludeImportStatements && element is KtImportDirective) return
        val diagnostic = deprecationDiagnostic(element)
        if (diagnostic != null) {
            report(
                Finding(
                    if (element is KtNamedDeclaration) Entity.atName(element) else Entity.from(element),
                    """${element.text} is deprecated with message "${diagnostic.message}""""
                )
            )
        }
        super.visitElement(element)
    }

    @OptIn(KaExperimentalApi::class)
    private fun deprecationDiagnostic(element: KtElement): KaFirDiagnostic.Deprecation? = analyze(element) {
        element
            .diagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
            .firstNotNullOfOrNull { it as? KaFirDiagnostic.Deprecation }
    }
}
