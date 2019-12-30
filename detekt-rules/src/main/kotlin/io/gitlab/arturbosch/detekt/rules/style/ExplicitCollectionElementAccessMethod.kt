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
 * Prefer usage of the indexed access operator [] for map or list element access or insert methods.
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
 *  map["key"]
 * </compliant>
 */
class ExplicitCollectionElementAccessMethod(config: Config = Config.empty) : Rule(config) {

    private val ktCollections = setOf("Map", "List")

    private val ktAndJavaCollections = ktCollections + setOf("AbstractMap", "AbstractList")

    override val issue: Issue =
        Issue(
            "ExplicitMapElementAccessMethod",
            Severity.Style,
            "Prefer usage of indexed access operator [] for map element access or insert methods",
            Debt.FIVE_MINS
        )

    override fun visitCallExpression(expression: KtCallExpression) {
        if (isGetOrPut(expression) && isMapMethod(expression)) {
            report(CodeSmell(issue, Entity.from(expression), "Prefer usage of indexed access operator []."))
        }
        super.visitCallExpression(expression)
    }

    private fun isGetOrPut(expression: KtCallExpression): Boolean {
        return expression
            .calleeExpression
            ?.text in setOf("get", "put")
    }

    @Suppress("ReturnCount")
    private fun isMapMethod(expression: KtCallExpression): Boolean {
        val dotExpression = expression.prevSibling
        if (dotExpression.parent !is KtDotQualifiedExpression) return false
        val caller = dotExpression.prevSibling
        if (caller !is KtElement) return false
        val callerReturnType = caller.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.returnType
        val standardTypeName = callerReturnType?.nameIfStandardType
        standardTypeName?.let {
            return it.toString() in ktCollections
        }
        val returnTypes = callerReturnType?.collectTypes()
        return returnTypes?.any { it.constructor.toString() in ktAndJavaCollections } ?: false
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
