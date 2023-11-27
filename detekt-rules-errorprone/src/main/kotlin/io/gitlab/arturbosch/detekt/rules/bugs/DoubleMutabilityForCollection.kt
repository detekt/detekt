package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext

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
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class DoubleMutabilityForCollection(config: Config = Config.empty) : Rule(config) {

    override val defaultRuleIdAliases: Set<String> = setOf("DoubleMutability")

    override val issue: Issue = Issue(
        "DoubleMutabilityForCollection",
        "Using var with mutable collections or values leads to double mutability. " +
            "Consider using val or immutable collection or value types.",
    )

    @Configuration("Define a list of mutable types to trigger on when defined with `var`.")
    private val mutableTypes: Set<FqName> by config(defaultMutableTypes) { types ->
        types.map { FqName(it) }.toSet()
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        val type = (bindingContext[BindingContext.VARIABLE, property])?.type ?: return
        val standardType = type.fqNameOrNull()
        if (property.isVar && standardType in mutableTypes) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(property),
                    "Variable ${property.name} is declared as `var` with a mutable type $standardType. " +
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
