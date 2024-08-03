package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.isNullable

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
    RequiresTypeResolution {
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
        report(CodeSmell(Entity.from(expression), message))
    }

    private fun KtExpression.isSimpleNameExpression(): Boolean =
        this is KtSimpleNameExpression || (this as? KtQualifiedExpression)?.selectorExpression is KtSimpleNameExpression

    private fun KtBinaryExpression.nullCheckedExpression(): KtExpression? {
        if (operationToken != KtTokens.EQEQ) return null
        return when {
            right.isNullKeyword() -> left
            left.isNullKeyword() -> right
            else -> null
        }?.takeIf { it.getType(bindingContext)?.isNullable() == true }
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

    private fun KtExpression?.isCalling(fqNames: List<FqName>): Boolean {
        val callExpression = this as? KtCallExpression
            ?: (this as? KtDotQualifiedExpression)?.selectorExpression as? KtCallExpression
            ?: return false
        return callExpression.calleeExpression?.text in fqNames.map { it.shortName().asString() } &&
            callExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() in fqNames
    }

    private fun KtExpression.classFqName() = getType(bindingContext)?.fqNameOrNull()

    private fun KtExpression.isCollectionOrArrayOrString(): Boolean {
        val classFqName = classFqName() ?: return false
        return classFqName() in collectionClasses || classFqName == arrayClass || classFqName == stringClass
    }

    private fun KtExpression.isCollectionOrArray(): Boolean {
        val classFqName = classFqName() ?: return false
        return classFqName() in collectionClasses || classFqName == arrayClass
    }

    private fun KtExpression.isString() = classFqName() == stringClass

    companion object {
        private val collectionClasses = listOf(
            StandardNames.FqNames.list,
            StandardNames.FqNames.set,
            StandardNames.FqNames.collection,
            StandardNames.FqNames.map,
            StandardNames.FqNames.mutableList,
            StandardNames.FqNames.mutableSet,
            StandardNames.FqNames.mutableCollection,
            StandardNames.FqNames.mutableMap,
        )

        private val arrayClass = StandardNames.FqNames.array.toSafe()

        private val stringClass = StandardNames.FqNames.string.toSafe()

        private val emptyCheckFunctions = collectionClasses.map { FqName("$it.isEmpty") } +
            listOf("kotlin.collections.isEmpty", "kotlin.text.isEmpty").map(::FqName)

        private val countFunctions = listOf("kotlin.collections.count", "kotlin.text.count").map(::FqName)
    }
}
