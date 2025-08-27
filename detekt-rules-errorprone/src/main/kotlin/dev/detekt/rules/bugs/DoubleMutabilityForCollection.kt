package dev.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Using `var` when declaring a mutable collection or value holder leads to double mutability.
 * Consider instead declaring your variable with `val` or switching your declaration to use an
 * immutable type.
 *
 * By default, the rule triggers on standard mutable collections, however it can be configured
 * to trigger on other types of mutable value types, such as `MutableState` from Jetpack
 * Compose.
 *
 * <noncompliant>
 * var myList = mutableListOf(1,2,3)
 * var mySet = mutableSetOf(1,2,3)
 * var myMap = mutableMapOf("answer" to 42)
 * </noncompliant>
 *
 * <compliant>
 * // Use val
 * val myList = mutableListOf(1,2,3)
 * val mySet = mutableSetOf(1,2,3)
 * val myMap = mutableMapOf("answer" to 42)
 *
 * // Use immutable types
 * var myList = listOf(1,2,3)
 * var mySet = setOf(1,2,3)
 * var myMap = mapOf("answer" to 42)
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
@Alias("DoubleMutability")
class DoubleMutabilityForCollection(config: Config) :
    Rule(
        config,
        "Using var with mutable collections or values leads to double mutability. " +
            "Consider using val or immutable collection or value types."
    ),
    RequiresAnalysisApi {

    @Configuration("Define a list of mutable types to trigger on when defined with `var`.")
    private val mutableTypes: Set<FqName> by config(defaultMutableTypes) { types ->
        types.map { FqName(it) }.toSet()
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        if (!property.isVar) return
        val type = analyze(property) { property.returnType.symbol?.classId?.asSingleFqName() }
        if (type in mutableTypes) {
            report(
                Finding(
                    Entity.from(property),
                    "Variable ${property.name} is declared as `var` with a mutable type $type. " +
                        "Consider using `val` or an immutable collection or value type"
                )
            )
        }
    }

    companion object {
        val defaultMutableTypes = listOf(
            "kotlin.collections.MutableList",
            "kotlin.collections.MutableMap",
            "kotlin.collections.MutableSet",
            "java.util.ArrayList",
            "java.util.LinkedHashSet",
            "java.util.HashSet",
            "java.util.LinkedHashMap",
            "java.util.HashMap",
        )
    }
}
