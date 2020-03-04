package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * In Kotlin functions `get` or `set` can be replaced with the shorter operator — `[]`,
 * see https://kotlinlang.org/docs/reference/operator-overloading.html#indexed.
 * Prefer the usage of the indexed access operator `[]` for map or list element access or insert methods.
 *
 * <noncompliant>
 *  val map = Map<String, String>()
 *  map.put("key", "value")
 *  val value = map.get("key")
 * </noncompliant>
 *
 * <compliant>
 *  val map = Map<String, String>()
 *  map["key"] = "value"
 *  val value = map["key"]
 * </compliant>
 */
class ExplicitCollectionElementAccessMethod(config: Config = Config.empty) : Rule(config) {

    private val ktCollections = setOf("Map", "MutableMap", "List", "MutableList")

    private val mapAccessMethods = setOf("get", "put")

    private val ktAndJavaCollections = ktCollections + setOf("AbstractMap", "AbstractList")

    override val issue: Issue =
        Issue(
            "ExplicitCollectionElementAccessMethod",
            Severity.Style,
            "Prefer usage of indexed access operator [] for map element access or insert methods",
            Debt.FIVE_MINS
        )

    override fun visitCallExpression(expression: KtCallExpression) {
        if (isMapMethod(expression) && isGetOrPut(expression)) {
            report(CodeSmell(issue, Entity.from(expression), "Prefer usage of indexed access operator []."))
        }
        super.visitCallExpression(expression)
    }

    private fun isGetOrPut(expression: KtCallExpression): Boolean {
        return expression.calleeExpression?.text in mapAccessMethods
    }

    private fun isMapMethod(expression: KtCallExpression): Boolean {
        val dotExpression = expression.prevSibling
        val caller = when (dotExpression?.parent) {
            is KtDotQualifiedExpression -> dotExpression.prevSibling
            else -> return false
        }
        return (caller as? KtElement).getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.returnType
            .isEligibleCollection()
    }

    private fun KotlinType?.isEligibleCollection(): Boolean {
        this?.nameIfStandardType?.let {
            return it.toString() in ktCollections
        }
        return this?.collectTypes()?.any { it.constructor.toString() in ktAndJavaCollections } == true
    }

    private fun KotlinType.collectTypes(): Set<KotlinType> {
        val result = mutableSetOf<KotlinType>()
        this
            .constructor
            .supertypes
            .forEach { type ->
                result.add(type)
                type.supertypes().forEach {
                    result.addAll(it.collectTypes())
                }
            }
        return result
    }
}
