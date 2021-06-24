package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.config
import io.gitlab.arturbosch.detekt.api.internal.configWithFallback
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * This rule reports a member that has the same as the containing class or object.
 * This might result in confusion.
 * The member should either be renamed or changed to a constructor.
 * Factory functions that create an instance of the class are exempt from this rule.
 *
 * <noncompliant>
 * class MethodNameEqualsClassName {
 *
 *     fun methodNameEqualsClassName() { }
 * }
 *
 * class PropertyNameEqualsClassName {
 *
 *     val propertyEqualsClassName = 0
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Manager {
 *
 *     companion object {
 *         // factory functions can have the same name as the class
 *         fun manager(): Manager {
 *             return Manager()
 *         }
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
class MemberNameEqualsClassName(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "A member should not be given the same name as its parent class or object.",
        Debt.FIVE_MINS
    )

    private val classMessage = "A member is named after the class. This might result in confusion. " +
        "Either rename the member or change it to a constructor."
    private val objectMessage = "A member is named after the object. " +
        "This might result in confusion. Please rename the member."

    @Suppress("unused")
    @Configuration("if overridden functions and properties should be ignored")
    @Deprecated("Use `ignoreOverridden` instead")
    private val ignoreOverriddenFunction: Boolean by config(true)

    @Configuration("if overridden functions and properties should be ignored")
    private val ignoreOverridden: Boolean by configWithFallback("ignoreOverriddenFunction", true)

    override fun visitClass(klass: KtClass) {
        if (!klass.isInterface()) {
            (getMisnamedMembers(klass, klass.name) + getMisnamedCompanionObjectMembers(klass))
                .forEach { report(CodeSmell(issue, Entity.from(it), classMessage)) }
        }
        super.visitClass(klass)
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        if (!declaration.isCompanion()) {
            getMisnamedMembers(declaration, declaration.name)
                .forEach { report(CodeSmell(issue, Entity.from(it), objectMessage)) }
        }
        super.visitObjectDeclaration(declaration)
    }

    private fun getMisnamedMembers(klassOrObject: KtClassOrObject, name: String?): Sequence<KtNamedDeclaration> {
        val body = klassOrObject.body ?: return emptySequence()
        return (body.functions.asSequence() as Sequence<KtNamedDeclaration> + body.properties)
            .filterNot { ignoreOverridden && it.isOverride() }
            .filter { it.name?.equals(name, ignoreCase = true) == true }
    }

    private fun getMisnamedCompanionObjectMembers(klass: KtClass): Sequence<KtNamedDeclaration> {
        return klass.companionObjects
            .asSequence()
            .flatMap { getMisnamedMembers(it, klass.name) }
            .filterNot { it is KtNamedFunction && isFactoryMethod(it, klass) }
    }

    private fun isFactoryMethod(function: KtNamedFunction, klass: KtClass): Boolean {
        val typeReference = function.typeReference
        return when {
            typeReference != null -> {
                val refName = (typeReference.typeElement as? KtUserType)?.referencedName ?: typeReference.text
                refName == klass.name
            }
            function.bodyExpression !is KtBlockExpression && bindingContext != BindingContext.EMPTY -> {
                val functionDescriptor =
                    bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, function] as? FunctionDescriptor
                val classDescriptor = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, klass]
                functionDescriptor?.returnType?.constructor?.declarationDescriptor == classDescriptor
            }
            else -> false
        }
    }
}
