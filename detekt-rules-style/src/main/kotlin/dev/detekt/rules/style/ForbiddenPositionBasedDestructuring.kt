package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaTypeParameterSymbol
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration

/**
 * Reports square-bracket destructuring of classes outside the configured allowlist.
 * Reordering properties of a data class can silently change which value is assigned to each variable.
 *
 * <noncompliant>
 * fun printRoute(route: Route) {
 *     val [origin, destination] = route
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun printRoute(route: Route) {
 *     val origin = route.origin
 *     val destination = route.destination
 * }
 * </compliant>
 */
class ForbiddenPositionBasedDestructuring(config: Config) :
    Rule(config, "Prefer name-based destructuring for classes whose component order may change."),
    RequiresAnalysisApi {

    @Configuration("Fully qualified names of classes that may be destructured by position.")
    private val allowedClasses: List<String> by config(listOf("kotlin.Pair", "kotlin.Triple"))

    override fun visitDestructuringDeclaration(destructuringDeclaration: KtDestructuringDeclaration) {
        super.visitDestructuringDeclaration(destructuringDeclaration)
        if (!destructuringDeclaration.hasSquareBrackets()) return

        analyze(destructuringDeclaration) {
            val type = destructuringDeclaration.returnType
            val name = if (type is KaTypeParameterType) {
                type.name.takeUnless { hasAllowedBound(type.symbol) }
            } else {
                type.expandedSymbol?.classId?.takeUnless { it.asFqNameString() in allowedClasses }?.shortClassName
            } ?: return
            report(Finding(Entity.from(destructuringDeclaration), "Avoid destructuring $name by position."))
        }
    }

    private fun KaSession.hasAllowedBound(
        parameter: KaTypeParameterSymbol,
        visited: MutableSet<KaTypeParameterSymbol> = mutableSetOf(),
    ): Boolean =
        visited.add(parameter) && parameter.upperBounds.any { bound ->
            when (bound) {
                is KaTypeParameterType -> hasAllowedBound(bound.symbol, visited)
                else -> bound.expandedSymbol?.classId?.asFqNameString() in allowedClasses
            }
        }
}
