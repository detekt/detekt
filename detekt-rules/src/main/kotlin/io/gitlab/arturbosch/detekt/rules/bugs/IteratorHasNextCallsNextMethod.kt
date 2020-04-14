package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
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
 *
 * @active since v1.2.0
 */
class IteratorHasNextCallsNextMethod(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("IteratorHasNextCallsNextMethod", Severity.Defect,
            "The hasNext() method of an Iterator implementation should not call the next() method. " +
                    "The state of the iterator should not be changed inside the hasNext() method. " +
                    "The hasNext() method is not supposed to have any side effects.",
            Debt.TEN_MINS)

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (classOrObject.getSuperNames().contains("Iterator")) {
            val hasNextMethod = classOrObject.findFunctionByName("hasNext")
            if (hasNextMethod != null && callsNextMethod(hasNextMethod)) {
                report(CodeSmell(issue, Entity.atName(classOrObject), "Calling hasNext() on an Iterator should " +
                        "have no side-effects. Calling next() is a side effect."))
            }
        }
        super.visitClassOrObject(classOrObject)
    }

    private fun callsNextMethod(method: KtNamedDeclaration): Boolean {
        return method.anyDescendantOfType<KtCallExpression> { it.calleeExpression?.text == "next" }
    }
}
