package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

/**
 * This rule detects null or empty checks that can be replaced with `isNullOrEmpty()` call.
 *
 * <noncompliant>
 * fun foo(x: List<Int>?) {
 *     if (x == null || x.isEmpty()) return
 * }
 * fun bar(x: List<Int>?) {
 *     if (x == null || x.count() == 0) return
 * }
 * fun baz(x: List<Int>?) {
 *     if (x == null || x.size == 0) return
 * }
 * </noncompliant>
 *
 * <compliant>
 * if (x.isNullOrEmpty()) return
 * </compliant>
 *
 */
@Suppress("TooManyFunctions")
@ActiveByDefault(since = "1.21.0")
class UseIsNullOrEmpty(config: Config) :
    Rule(
        config,
        "Use `isNullOrEmpty()` call instead of `x == null || x.isEmpty()`."
    ),
    RequiresAnalysisApi {

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (expression.operationToken != KtTokens.OROR) return
        val left = expression.left as? KtBinaryExpression ?: return
        val right = expression.right ?: return

        val nullCheckedExpression = left.nullCheckedExpression() ?: return
        val sizeCheckedExpression = right.sizeCheckedExpression() ?: return
        if (!nullCheckedExpression.isSimpleNameExpression() || !sizeCheckedExpression.isSimpleNameExpression()) return
        if (nullCheckedExpression.text != sizeCheckedExpression.text) return

        val message = "This '${expression.text}' can be replaced with 'isNullOrEmpty()' call"
        report(Finding(Entity.from(expression), message))
    }

    private fun KtExpression.isSimpleNameExpression(): Boolean =
        this is KtSimpleNameExpression || (this as? KtQualifiedExpression)?.selectorExpression is KtSimpleNameExpression

    private fun KtBinaryExpression.nullCheckedExpression(): KtExpression? {
        if (operationToken != KtTokens.EQEQ) return null
        return when {
            right.isNullKeyword() -> left
            left.isNullKeyword() -> right
            else -> null
        }?.takeIf {
            analyze(it) {
                val expressionType = it.expressionType
                expressionType?.nullability?.isNullable == true || expressionType?.hasFlexibleNullability == true
            }
        }
    }

    private fun KtExpression.sizeCheckedExpression(): KtExpression? =
        when (this) {
            is KtDotQualifiedExpression -> sizeCheckedExpression()
            is KtBinaryExpression -> sizeCheckedExpression()
            else -> null
        }

    private fun KtDotQualifiedExpression.sizeCheckedExpression(): KtExpression? {
        if (!selectorExpression.isCalling(emptyCheckFunctions)) return null
        return receiverExpression.takeIf { it.isCollectionOrArrayOrString() }
    }

    private fun KtBinaryExpression.sizeCheckedExpression(): KtExpression? {
        if (operationToken != KtTokens.EQEQ) return null
        return when {
            right.isEmptyString() -> left?.sizeCheckedEmptyString()
            left.isEmptyString() -> right?.sizeCheckedEmptyString()
            right.isZero() -> left?.sizeCheckedEqualToZero()
            left.isZero() -> right?.sizeCheckedEqualToZero()
            else -> null
        }
    }

    private fun KtExpression.sizeCheckedEmptyString(): KtExpression? = takeIf { it.isString() }

    @Suppress("ReturnCount")
    private fun KtExpression.sizeCheckedEqualToZero(): KtExpression? {
        if (this !is KtDotQualifiedExpression) return null
        val receiver = receiverExpression
        val selector = selectorExpression ?: return null
        when {
            selector is KtCallExpression ->
                if (!receiver.isCollectionOrArrayOrString() || !selector.isCalling(countFunctions)) return null
            selector.text == "size" ->
                if (!receiver.isCollectionOrArray()) return null
            selector.text == "length" ->
                if (!receiver.isString()) return null
        }
        return receiver
    }

    private fun KtExpression?.isNullKeyword() = this?.text == KtTokens.NULL_KEYWORD.value

    private fun KtExpression?.isZero() = this?.text == "0"

    private fun KtExpression?.isEmptyString() = this?.text == "\"\""

    private fun KtExpression?.isCalling(callableIds: List<CallableId>): Boolean {
        val callExpression = this as? KtCallExpression
            ?: (this as? KtDotQualifiedExpression)?.selectorExpression as? KtCallExpression
            ?: return false
        if (callExpression.calleeExpression?.text !in callableIds.map { it.asSingleFqName().shortName().asString() }) {
            return false
        }
        return analyze(callExpression) {
            callExpression.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId in callableIds
        }
    }

    private fun KtExpression.classId(): ClassId? {
        val expression = this
        return analyze(expression) {
            val type = expression.expressionType
            type?.symbol?.classId ?: type?.upperBoundIfFlexible()?.symbol?.classId
        }
    }

    private fun KtExpression.isCollectionOrArrayOrString(): Boolean {
        val classFqName = classId() ?: return false
        return classFqName in collectionClasses || classFqName == arrayClass || classFqName == stringClass
    }

    private fun KtExpression.isCollectionOrArray(): Boolean {
        val classFqName = classId() ?: return false
        return classFqName in collectionClasses || classFqName == arrayClass
    }

    private fun KtExpression.isString() = classId() == stringClass

    companion object {
        private val collectionClasses = listOf(
            StandardClassIds.List,
            StandardClassIds.Set,
            StandardClassIds.Collection,
            StandardClassIds.Map,
            StandardClassIds.MutableList,
            StandardClassIds.MutableSet,
            StandardClassIds.MutableCollection,
            StandardClassIds.MutableMap,
        )

        private val arrayClass = StandardClassIds.Array

        private val stringClass = StandardClassIds.String

        private val emptyCheckFunctions = buildList {
            val callableName = Name.identifier("isEmpty")
            addAll(collectionClasses.map { CallableId(it, callableName) })
            add(CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, callableName))
            add(CallableId(StandardClassIds.BASE_TEXT_PACKAGE, callableName))
        }

        private val countFunctions = buildList {
            val callableName = Name.identifier("count")
            add(CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, callableName))
            add(CallableId(StandardClassIds.BASE_TEXT_PACKAGE, callableName))
        }
    }
}
