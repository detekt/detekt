package dev.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtUserType

/**
 * This rule reports a member that has the same name as the containing class or object.
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
class MemberNameEqualsClassName(config: Config) :
    Rule(
        config,
        "A member should not be given the same name as its parent class or object."
    ),
    RequiresAnalysisApi {

    private val classMessage = "A member is named after the class. This might result in confusion. " +
        "Either rename the member or change it to a constructor."
    private val objectMessage = "A member is named after the object. " +
        "This might result in confusion. Please rename the member."

    @Configuration("if overridden functions and properties should be ignored")
    private val ignoreOverridden: Boolean by config(true)

    override fun visitClass(klass: KtClass) {
        if (!klass.isInterface()) {
            (getMisnamedMembers(klass, klass.name) + getMisnamedCompanionObjectMembers(klass))
                .forEach { report(Finding(Entity.from(it), classMessage)) }
        }
        super.visitClass(klass)
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        if (!declaration.isCompanion()) {
            getMisnamedMembers(declaration, declaration.name)
                .forEach { report(Finding(Entity.from(it), objectMessage)) }
        }
        super.visitObjectDeclaration(declaration)
    }

    private fun getMisnamedMembers(klassOrObject: KtClassOrObject, name: String?): Sequence<KtNamedDeclaration> {
        val body = klassOrObject.body ?: return emptySequence()
        return (body.functions.asSequence() as Sequence<KtNamedDeclaration> + body.properties)
            .filterNot { ignoreOverridden && it.isOverride() }
            .filter { it.name?.equals(name, ignoreCase = true) == true }
    }

    private fun getMisnamedCompanionObjectMembers(klass: KtClass): Sequence<KtNamedDeclaration> =
        klass.companionObjects
            .asSequence()
            .flatMap { getMisnamedMembers(it, klass.name) }
            .filterNot { it is KtNamedFunction && isFactoryMethod(it, klass) }

    private fun isFactoryMethod(function: KtNamedFunction, klass: KtClass): Boolean {
        val typeReference = function.typeReference
        return when {
            typeReference != null -> {
                val refName = (typeReference.typeElement as? KtUserType)?.referencedName ?: typeReference.text
                refName == klass.name
            }

            function.bodyExpression is KtBlockExpression -> false

            function.bodyExpression !is KtBlockExpression -> {
                analyze(function) {
                    klass.symbol == function.returnType.symbol
                }
            }

            else -> true // We don't know if it is or not a factory. We assume it is a factory to avoid false-positives
        }
    }
}
