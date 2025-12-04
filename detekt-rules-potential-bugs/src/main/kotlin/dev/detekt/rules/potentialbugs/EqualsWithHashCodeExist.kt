package dev.detekt.rules.potentialbugs

import com.intellij.psi.PsiFile
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.psi.isEqualsFunction
import dev.detekt.psi.isHashCodeFunction
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

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
 */
@ActiveByDefault(since = "1.0.0")
class EqualsWithHashCodeExist(config: Config) : Rule(
    config,
    "Always override hashCode when you override equals. " +
        "All hash-based collections depend on objects meeting the equals-contract. " +
        "Two equal objects must produce the same hashcode. When inheriting equals or hashcode, " +
        "override the inherited and call the super method for clarification."
) {

    private val queue = ArrayDeque<ViolationHolder>(MAXIMUM_EXPECTED_NESTED_CLASSES)

    private data class ViolationHolder(var equals: Boolean = false, var hashCode: Boolean = false) {
        fun violation() = equals && !hashCode || !equals && hashCode
    }

    override fun visitFile(file: PsiFile) {
        queue.clear()
        super.visitFile(file)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (classOrObject is KtClass && classOrObject.isData()) return

        queue.addFirst(ViolationHolder())
        super.visitClassOrObject(classOrObject)
        if (queue.removeFirst().violation()) {
            report(
                Finding(
                    Entity.atName(classOrObject),
                    "A class should always override hashCode " +
                        "when overriding equals and the other way around."
                )
            )
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!function.isTopLevel) {
            if (function.isEqualsFunction()) queue.first().equals = true
            if (function.isHashCodeFunction()) queue.first().hashCode = true
        }
    }
}

private const val MAXIMUM_EXPECTED_NESTED_CLASSES = 5
