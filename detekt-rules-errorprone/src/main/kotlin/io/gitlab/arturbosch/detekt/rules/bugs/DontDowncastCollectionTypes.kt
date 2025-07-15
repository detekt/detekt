package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * Down-casting immutable types from kotlin.collections should be discouraged.
 * The result of the downcast is platform specific and can lead to unexpected crashes.
 * Prefer to use instead the `toMutable<Type>()` functions.
 *
 * <noncompliant>
 * val list : List<Int> = getAList()
 * if (list is MutableList) {
 *     list.add(42)
 * }
 *
 * (list as MutableList).add(42)
 * </noncompliant>
 *
 * <compliant>
 * val list : List<Int> = getAList()
 * list.toMutableList().add(42)
 * </compliant>
 *
 */
class DontDowncastCollectionTypes(config: Config) :
    Rule(
        config,
        "Down-casting immutable collection types is breaking the collection contract."
    ),
    RequiresAnalysisApi {

    override fun visitIsExpression(expression: KtIsExpression) {
        super.visitIsExpression(expression)

        analyze(expression) {
            checkForDowncast(expression, expression.leftHandSide, expression.typeReference)
        }
    }

    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)

        analyze(expression) {
            checkForDowncast(expression, expression.left, expression.right)
        }
    }

    private fun KaSession.checkForDowncast(parent: KtExpression, left: KtExpression, right: KtTypeReference?) {
        val leftType = left.expressionType?.symbol?.classId ?: return
        val rightType = right?.type?.symbol?.classId ?: return

        if (rightType in mutableTypes[leftType].orEmpty()) {
            val leftClassName = leftType.shortClassName.asString()
            val rightClassName = rightType.shortClassName.asString()
            var message = "Down-casting from type $leftClassName to $rightClassName is risky."
            if (rightClassName.startsWith("Mutable")) {
                message += " Use `to$rightClassName()` instead."
            }
            report(Finding(Entity.from(parent), message))
        }
    }

    companion object {
        // Kotlin Stdlib Mutable types plus Type-aliases from:
        // https://github.com/JetBrains/kotlin/blob/46b7a774b558001c136be225cf4367fa09ba1aee/libraries/stdlib/jvm/src/kotlin/collections/TypeAliases.kt#L13-L17
        private val mutableTypes = mapOf(
            StandardClassIds.List to listOf(
                StandardClassIds.MutableList,
                ClassId.fromString("java/util/ArrayList"),
            ),
            StandardClassIds.Set to listOf(
                StandardClassIds.MutableSet,
                ClassId.fromString("java/util/LinkedHashSet"),
                ClassId.fromString("java/util/HashSet"),
            ),
            StandardClassIds.Map to listOf(
                StandardClassIds.MutableMap,
                ClassId.fromString("java/util/LinkedHashMap"),
                ClassId.fromString("java/util/HashMap"),
            ),
        )
    }
}
