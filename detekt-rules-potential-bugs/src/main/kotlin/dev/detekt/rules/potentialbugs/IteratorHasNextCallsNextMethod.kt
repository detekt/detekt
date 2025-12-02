package dev.detekt.rules.potentialbugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames

/**
 * Verifies implementations of the Iterator interface.
 * The hasNext() method of an Iterator implementation should not have any side effects.
 * This rule reports implementations that call the next() method of the Iterator inside the hasNext() method.
 *
 * <noncompliant>
 * class MyIterator : Iterator<String> {
 *
 *     override fun hasNext(): Boolean {
 *         return next() != null
 *     }
 * }
 * </noncompliant>
 */
@ActiveByDefault(since = "1.2.0")
class IteratorHasNextCallsNextMethod(config: Config) : Rule(
    config,
    "The `hasNext()` method of an Iterator implementation should not call the `next()` method. " +
        "The state of the iterator should not be changed inside the `hasNext()` method. " +
        "The `hasNext()` method is not supposed to have any side effects."
) {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (classOrObject.getSuperNames().contains("Iterator")) {
            val hasNextMethod = classOrObject.findFunctionByName("hasNext")
            if (hasNextMethod != null && callsNextMethod(hasNextMethod)) {
                report(
                    Finding(
                        Entity.atName(classOrObject),
                        "Calling hasNext() on an Iterator should " +
                            "have no side-effects. Calling next() is a side effect."
                    )
                )
            }
        }
        super.visitClassOrObject(classOrObject)
    }

    private fun callsNextMethod(method: KtNamedDeclaration): Boolean =
        method.anyDescendantOfType<KtCallExpression> { it.calleeExpression?.text == "next" }
}
