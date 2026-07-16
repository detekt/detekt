package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolOrigin
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDelegatedSuperTypeEntry

/**
 * Reports classes that implement a Java interface by delegation when that interface has default
 * methods that the class does not forward.
 *
 * Kotlin does not generate delegating members for the default methods of a Java interface. The
 * class inherits the default implementation instead of forwarding the call, so whatever the
 * delegate does for those methods is ignored. Override them and forward to the delegate. See
 * [KT-15226](https://youtrack.jetbrains.com/issue/KT-15226).
 *
 * Kotlin collection types such as `List` are excluded. Their mapped `java.util` defaults are not
 * forwarded either, but those defaults read members that delegation does forward, so a delegate
 * that does not override them itself still supplies the result.
 *
 * <noncompliant>
 * // `JavaInterface` is written in Java and declares a default method `optional()`.
 * class Wrapper(private val delegate: JavaInterface) : JavaInterface by delegate
 * </noncompliant>
 *
 * <compliant>
 * // `JavaInterface` is written in Java and declares a default method `optional()`.
 * class Wrapper(private val delegate: JavaInterface) : JavaInterface by delegate {
 *     override fun optional() {
 *         delegate.optional()
 *     }
 * }
 * </compliant>
 */
class IgnoredJavaDefaultMethod(config: Config) :
    Rule(
        config,
        "Delegated Java interface has default methods that are not forwarded.",
    ),
    RequiresAnalysisApi {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)

        val delegatedEntries = classOrObject.superTypeListEntries
            .filterIsInstance<KtDelegatedSuperTypeEntry>()
        if (delegatedEntries.isEmpty()) return

        analyze(classOrObject) {
            val javaInterfaces = delegatedEntries.mapNotNull { entry ->
                val type = entry.typeReference?.type ?: return@mapNotNull null
                val symbol = type.symbol as? KaClassSymbol ?: return@mapNotNull null
                if (symbol.classKind != KaClassKind.INTERFACE) return@mapNotNull null
                if (!hasJavaDeclaredAncestor(type)) return@mapNotNull null
                entry to symbol
            }
            if (javaInterfaces.isEmpty()) return@analyze

            val classSymbol = classOrObject.symbol as? KaClassSymbol ?: return@analyze
            // The methods the class ends up with that still come straight from Java. Keyed by the
            // original declaration so an overload of the same name cannot stand in for another.
            val ignoredOriginals = classSymbol.memberScope
                .callables
                .filterIsInstance<KaNamedFunctionSymbol>()
                .filter { isIgnoredJavaDefault(it) }
                .map { it.fakeOverrideOriginal }
                .toSet()
            if (ignoredOriginals.isEmpty()) return@analyze

            javaInterfaces.forEach { (entry, symbol) ->
                reportIgnoredDefaultMethods(entry, symbol, ignoredOriginals)
            }
        }
    }

    private fun KaSession.reportIgnoredDefaultMethods(
        entry: KtDelegatedSuperTypeEntry,
        interfaceSymbol: KaClassSymbol,
        ignoredOriginals: Set<KaCallableSymbol>,
    ) {
        val ignored = interfaceSymbol.memberScope
            .callables
            .filterIsInstance<KaNamedFunctionSymbol>()
            .filter { isIgnoredJavaDefault(it) }
            .filter { it.fakeOverrideOriginal in ignoredOriginals }
            .map { it.name.asString() }
            .distinct()
            .sorted()
            .toList()

        if (ignored.isEmpty()) return

        val single = ignored.size == 1
        report(
            Finding(
                Entity.from(entry),
                "Delegating to `${interfaceSymbol.name?.asString()}` ignores what the delegate " +
                    "does for the default ${if (single) "method" else "methods"} " +
                    "${ignored.joinToString { "`$it`" }}. " +
                    "Override ${if (single) "it" else "them"} to delegate explicitly.",
            )
        )
    }

    // Kotlin maps its own collection types onto java.util ones without a Java type in the
    // hierarchy, so requiring a Java-declared interface here keeps them out.
    private fun KaSession.hasJavaDeclaredAncestor(type: KaType): Boolean =
        (sequenceOf(type) + type.allSupertypes).any { supertype ->
            (supertype.symbol as? KaClassSymbol)
                ?.let { it.classKind == KaClassKind.INTERFACE && it.isDeclaredInJava() } == true
        }

    // The origin comes off the original because generic substitution hides the Java one.
    // The original turns Kotlin as soon as anything in Kotlin overrides the method, and a
    // generated delegating member drops out on its own DELEGATED origin.
    // Public and not abstract leaves out the Java 9 private interface methods.
    private fun KaSession.isIgnoredJavaDefault(symbol: KaNamedFunctionSymbol): Boolean =
        symbol.fakeOverrideOriginal.isDeclaredInJava() &&
            symbol.origin != KaSymbolOrigin.DELEGATED &&
            symbol.modality != KaSymbolModality.ABSTRACT &&
            symbol.visibility == KaSymbolVisibility.PUBLIC

    private fun KaSymbol.isDeclaredInJava(): Boolean =
        origin == KaSymbolOrigin.JAVA_SOURCE || origin == KaSymbolOrigin.JAVA_LIBRARY
}
