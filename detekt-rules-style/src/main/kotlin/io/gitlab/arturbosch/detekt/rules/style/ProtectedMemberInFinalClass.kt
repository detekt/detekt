package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isJvmFinalizeFunction
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.psi.psiUtil.isProtected

/**
 * Kotlin classes are `final` by default. Thus classes which are not marked as `open` should not contain any `protected`
 * members. Consider using `private` or `internal` modifiers instead.
 *
 * <noncompliant>
 * class ProtectedMemberInFinalClass {
 *     protected var i = 0
 * }
 * </noncompliant>
 *
 * <compliant>
 * class ProtectedMemberInFinalClass {
 *     private var i = 0
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class ProtectedMemberInFinalClass(config: Config) : Rule(
    config,
    "Member with protected visibility in final class is private. Consider using private or internal as modifier."
) {

    private val visitor = DeclarationVisitor()

    /**
     * Only classes and companion objects can contain protected members.
     */
    override fun visitClass(klass: KtClass) {
        if (hasModifiers(klass)) {
            klass.primaryConstructor?.accept(visitor)
            klass.body?.declarations?.forEach { it.accept(visitor) }
            klass.companionObjects.forEach { it.accept(visitor) }
        }
        super.visitClass(klass)
    }

    private fun hasModifiers(klass: KtClass): Boolean {
        val isNotAbstract = !klass.isAbstract()
        val isFinal = !klass.isOpen()
        val isNotSealed = !klass.isSealed()
        val isNotEnum = !klass.isEnum()
        return isNotAbstract && isFinal && isNotSealed && isNotEnum
    }

    internal inner class DeclarationVisitor : DetektVisitor() {

        override fun visitDeclaration(dcl: KtDeclaration) {
            val isJvmFinalizeFunction = dcl is KtNamedFunction && dcl.isJvmFinalizeFunction()

            if (dcl.isProtected() && !dcl.isOverride() && !isJvmFinalizeFunction) {
                report(Finding(Entity.from(dcl), description))
            }
        }
    }
}
