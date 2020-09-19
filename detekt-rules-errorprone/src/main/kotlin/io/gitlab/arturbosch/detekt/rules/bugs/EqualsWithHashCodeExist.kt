package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isEqualsFunction
import io.gitlab.arturbosch.detekt.rules.isHashCodeFunction
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.util.ArrayDeque

/**
 * When a class overrides the equals() method it should also override the hashCode() method.
 *
 * All hash-based collections depend on objects meeting the equals-contract. Two equal objects must produce the
 * same hashcode. When inheriting equals or hashcode, override the inherited and call the super method for
 * clarification.
 *
 * <noncompliant>
 * class Foo {
 *
 *     override fun equals(other: Any?): Boolean {
 *         return super.equals(other)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Foo {
 *
 *     override fun equals(other: Any?): Boolean {
 *         return super.equals(other)
 *     }
 *
 *     override fun hashCode(): Int {
 *         return super.hashCode()
 *     }
 * }
 * </compliant>
 *
 * @active since v1.0.0
 */
class EqualsWithHashCodeExist(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("EqualsWithHashCodeExist",
            Severity.Defect,
            "Always override hashCode when you override equals. " +
                    "All hash-based collections depend on objects meeting the equals-contract. " +
                    "Two equal objects must produce the same hashcode. When inheriting equals or hashcode, " +
                    "override the inherited and call the super method for clarification.",
            Debt.FIVE_MINS)

    private val queue = ArrayDeque<ViolationHolder>(MAXIMUM_EXPECTED_NESTED_CLASSES)

    private data class ViolationHolder(var equals: Boolean = false, var hashCode: Boolean = false) {
        fun violation() = equals && !hashCode || !equals && hashCode
    }

    override fun visitFile(file: PsiFile?) {
        queue.clear()
        super.visitFile(file)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (classOrObject is KtClass && classOrObject.isData()) return

        queue.push(ViolationHolder())
        super.visitClassOrObject(classOrObject)
        if (queue.pop().violation()) {
            report(CodeSmell(issue, Entity.atName(classOrObject), "A class should always override hashCode " +
                    "when overriding equals and the other way around."))
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!function.isTopLevel) {
            if (function.isEqualsFunction()) queue.peek().equals = true
            if (function.isHashCodeFunction()) queue.peek().hashCode = true
        }
    }
}

private const val MAXIMUM_EXPECTED_NESTED_CLASSES = 5
