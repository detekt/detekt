package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames

/**
 * Reports implementations of the `Iterator` interface which do not throw a NoSuchElementException in the
 * implementation of the next() method. When there are no more elements to return an Iterator should throw a
 * NoSuchElementException.
 *
 * See: https://docs.oracle.com/javase/7/docs/api/java/util/Iterator.html#next()
 *
 * <noncompliant>
 * class MyIterator : Iterator<String> {
 *
 *     override fun next(): String {
 *         return ""
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * class MyIterator : Iterator<String> {
 *
 *     override fun next(): String {
 *         if (!this.hasNext()) {
 *             throw NoSuchElementException()
 *         }
 *         // ...
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class IteratorNotThrowingNoSuchElementException(config: Config) : Rule(
    config,
    "The `next()` method of an `Iterator` implementation should throw a `NoSuchElementException` " +
        "when there are no more elements to return."
) {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (classOrObject.getSuperNames().contains("Iterator")) {
            val nextMethod = classOrObject.findFunctionByName("next")
            if (nextMethod != null && !nextMethod.throwsNoSuchElementExceptionThrown()) {
                report(
                    Finding(
                        Entity.atName(classOrObject),
                        "This implementation of Iterator does not correctly implement the next() method as " +
                            "it doesn't throw a NoSuchElementException when no elements remain in the Iterator."
                    )
                )
            }
        }
        super.visitClassOrObject(classOrObject)
    }

    private fun KtNamedDeclaration.throwsNoSuchElementExceptionThrown() =
        anyDescendantOfType<KtThrowExpression> { isNoSuchElementExpression(it) }

    private fun isNoSuchElementExpression(expression: KtThrowExpression): Boolean {
        val calleeExpression = (expression.thrownExpression as? KtCallExpression)?.calleeExpression
        return calleeExpression?.text == "NoSuchElementException"
    }
}
