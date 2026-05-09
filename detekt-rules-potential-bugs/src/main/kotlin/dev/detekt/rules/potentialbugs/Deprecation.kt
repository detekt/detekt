package dev.detekt.rules.potentialbugs

import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaFirDiagnostic
import org.jetbrains.kotlin.psi.KtElement
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

    override fun visitKtElement(element: KtElement) {
        val diagnostic = deprecationDiagnostic(element)
        if (diagnostic != null) {
            report(
                Finding(
                    if (element is KtNamedDeclaration) Entity.atName(element) else Entity.from(element),
                    """${element.text} is deprecated with message "$diagnostic""""
                )
            )
        }
        super.visitElement(element)
    }

    @OptIn(KaExperimentalApi::class)
    private fun deprecationDiagnostic(element: KtElement): String? =
        analyze(element) {
            element
                .diagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
                .firstNotNullOfOrNull { it as? KaFirDiagnostic.Deprecation }
                ?.message
        }
}
