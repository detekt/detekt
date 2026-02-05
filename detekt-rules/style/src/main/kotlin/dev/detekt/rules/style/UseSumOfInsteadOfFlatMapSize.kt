package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis

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
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val calleeText = expression.calleeExpression?.text ?: return
        if (!expression.isFlatMapOrFlatten()) return

        val receiver = expression.getQualifiedExpressionForSelectorOrThis()
        val selector = receiver.getQualifiedExpressionForReceiver()?.selectorExpression ?: return
        if (!selector.isSizeOrCount(receiver)) return

        val selectorText = (selector as? KtCallExpression)?.calleeExpression?.text ?: selector.text
        val message = "Use 'sumOf' instead of '$calleeText' and '$selectorText'"
        report(Finding(Entity.from(expression), message))
    }

    private fun KtCallExpression.isFlatMapOrFlatten(): Boolean = isCalling(flatMapAndFlattenCallableId)

    private fun KtExpression.isSizeOrCount(receiver: KtExpression) =
        if ((this as? KtNameReferenceExpression)?.text == "size") {
            analyze(receiver) { receiver.expressionType?.isSubtypeOf(StandardClassIds.List) == true }
        } else {
            (this as? KtCallExpression)?.isCalling(countCallableId) == true
        }

    companion object {
        private val flatMapAndFlattenCallableId = listOf(
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("flatMap")),
            CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("flatten")),
        )

        private val countCallableId = CallableId(StandardClassIds.BASE_COLLECTIONS_PACKAGE, Name.identifier("count"))
    }
}
