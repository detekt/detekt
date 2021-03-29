package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

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
@RequiresTypeResolution
@Suppress("TooManyFunctions")
class UseIsNullOrEmpty(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        "UseIsNullOrEmpty",
        Severity.Style,
        "Use 'isNullOrEmpty()' call instead of 'x == null || x.isEmpty()'",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        if (expression.operationToken != KtTokens.OROR) return
        val left = expression.left as? KtBinaryExpression ?: return
        val right = expression.right ?: return

        val nullCheckedExpression = left.nullCheckedExpression() ?: return
        val sizeCheckedExpression = right.sizeCheckedExpression() ?: return
        if (nullCheckedExpression.text != sizeCheckedExpression.text) return

        val message = "This '${expression.text}' can be replaced with 'isNullOrEmpty()' call"
        report(CodeSmell(issue, Entity.from(expression), message))
    }

    private fun KtBinaryExpression.nullCheckedExpression(): KtSimpleNameExpression? {
        if (operationToken != KtTokens.EQEQ) return null
        return when {
            right.isNullKeyword() -> left
            left.isNullKeyword() -> right
            else -> null
        }.safeAs<KtSimpleNameExpression>()?.takeIf { it.getType(bindingContext)?.isNullable() == true }
    }

    private fun KtExpression.sizeCheckedExpression(): KtSimpleNameExpression? {
        return when (this) {
            is KtDotQualifiedExpression -> sizeCheckedExpression()
            is KtBinaryExpression -> sizeCheckedExpression()
            else -> null
        }
    }

    private fun KtDotQualifiedExpression.sizeCheckedExpression(): KtSimpleNameExpression? {
        if (!selectorExpression.isCalling(isEmptyFunctions)) return null
        return receiverExpression.safeAs<KtSimpleNameExpression>()?.takeIf { it.isCollectionOrArrayOrString() }
    }

    private fun KtBinaryExpression.sizeCheckedExpression(): KtSimpleNameExpression? {
        if (operationToken != KtTokens.EQEQ) return null
        return when {
            right.isEmptyString() -> left?.sizeCheckedEmptyString()
            left.isEmptyString() -> right?.sizeCheckedEmptyString()
            right.isZero() -> left?.sizeCheckedEqualToZero()
            left.isZero() -> right?.sizeCheckedEqualToZero()
            else -> null
        }
    }

    private fun KtExpression.sizeCheckedEmptyString(): KtSimpleNameExpression? {
        return safeAs<KtSimpleNameExpression>()?.takeIf { it.isString() }
    }

    @Suppress("ReturnCount")
    private fun KtExpression.sizeCheckedEqualToZero(): KtSimpleNameExpression? {
        if (this !is KtDotQualifiedExpression) return null
        val receiver = receiverExpression as? KtSimpleNameExpression ?: return null
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
        val callExpression = safeAs()
            ?: safeAs<KtDotQualifiedExpression>()?.selectorExpression.safeAs<KtCallExpression>()
            ?: return false
        return callExpression.calleeExpression?.text in fqNames.map { it.shortName().asString() } &&
            callExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() in fqNames
    }

    private fun KtSimpleNameExpression.classFqName() = getType(bindingContext)?.fqNameOrNull()

    private fun KtSimpleNameExpression.isCollectionOrArrayOrString(): Boolean {
        val classFqName = classFqName() ?: return false
        return classFqName() in collectionClasses || classFqName == arrayClass || classFqName == stringClass
    }

    private fun KtSimpleNameExpression.isCollectionOrArray(): Boolean {
        val classFqName = classFqName() ?: return false
        return classFqName() in collectionClasses || classFqName == arrayClass
    }

    private fun KtSimpleNameExpression.isString() = classFqName() == stringClass

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

        private val isEmptyFunctions = collectionClasses.map { FqName("$it.isEmpty") } +
            listOf("kotlin.collections.isEmpty", "kotlin.text.isEmpty").map(::FqName)

        private val countFunctions = listOf("kotlin.collections.count", "kotlin.text.count").map(::FqName)
    }
}
