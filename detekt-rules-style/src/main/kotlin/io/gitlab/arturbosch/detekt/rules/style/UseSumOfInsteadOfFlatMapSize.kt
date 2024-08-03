package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isCalling
import org.jetbrains.kotlin.builtins.DefaultBuiltIns
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.isSubclassOf

/**
 * Turn on this rule to flag `flatMap` and `size/count` calls that can be replaced with a `sumOf` call.
 *
 * <noncompliant>
 * class Foo(val foo: List<Int>)
 * list.flatMap { it.foo }.size
 * list.flatMap { it.foo }.count()
 * list.flatMap { it.foo }.count { it > 2 }
 * listOf(listOf(1), listOf(2, 3)).flatten().size
 * </noncompliant>
 *
 * <compliant>
 * list.sumOf { it.foo.size }
 * list.sumOf { it.foo.count() }
 * list.sumOf { it.foo.count { foo -> foo > 2 } }
 * listOf(listOf(1), listOf(2, 3)).sumOf { it.size }
 * </compliant>
 */
class UseSumOfInsteadOfFlatMapSize(config: Config) :
    Rule(
        config,
        "Use `sumOf` instead of `flatMap` and `size/count` calls"
    ),
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val calleeText = expression.calleeExpression?.text ?: return
        if (!expression.isFlatMapOrFlatten()) return

        val receiver = expression.getQualifiedExpressionForSelectorOrThis()
        val selector = receiver.getQualifiedExpressionForReceiver()?.selectorExpression ?: return
        if (!selector.isSizeOrCount(receiver)) return

        val selectorText = (selector as? KtCallExpression)?.calleeExpression?.text ?: selector.text
        val message = "Use 'sumOf' instead of '$calleeText' and '$selectorText'"
        report(CodeSmell(Entity.from(expression), message))
    }

    private fun KtCallExpression.isFlatMapOrFlatten(): Boolean = isCalling(flatMapAndFlattenFqName, bindingContext)

    private fun KtExpression.isSizeOrCount(receiver: KtExpression): Boolean {
        if ((this as? KtNameReferenceExpression)?.text == "size") {
            val receiverType = receiver.getType(bindingContext) ?: return false
            val descriptor = receiverType.constructor.declarationDescriptor as? ClassDescriptor ?: return false
            return descriptor.isSubclassOf(DefaultBuiltIns.Instance.list)
        }
        return (this as? KtCallExpression)?.isCalling(countFqName, bindingContext) == true
    }

    companion object {
        private val flatMapAndFlattenFqName = listOf(
            FqName("kotlin.collections.flatMap"),
            FqName("kotlin.collections.flatten"),
        )

        private val countFqName = FqName("kotlin.collections.count")
    }
}
