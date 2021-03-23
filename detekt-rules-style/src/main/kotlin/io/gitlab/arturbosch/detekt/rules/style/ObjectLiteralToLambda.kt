package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtSuperTypeEntry
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

/**
 * An anonymous object that does nothing other than the implementation of a single method
 * can be used as a lambda.
 *
 * See https://kotlinlang.org/docs/fun-interfaces.html
 *
 * <noncompliant>
 * object : Foo {
 *     override fun bar() {
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * Foo {
 * }
 * </compliant>
 */
class ObjectLiteralToLambda(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Report object literals that can be changed to lambdas.",
        Debt.FIVE_MINS
    )

    private val KotlinType.isSamInterface
        get() = (constructor.declarationDescriptor as? ClassDescriptor)
            ?.isDefinitelyNotSamInterface == false

    private fun KtObjectDeclaration.hasSingleOverriddenMethod(): Boolean =
        name == null &&
            superTypeListEntries.size == 1 &&
            superTypeListEntries[0] is KtSuperTypeEntry &&
            hasOneNamedOverriddenMethod()

    private fun KtObjectDeclaration.hasOneNamedOverriddenMethod(): Boolean =
        declarations.size == 1 &&
            (declarations[0] as? KtNamedFunction)?.isOverride() == true

    private fun KtObjectLiteralExpression.singleSuperType() =
        bindingContext.getType(this)?.constructor?.supertypes?.firstOrNull()

    override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
        super.visitObjectLiteralExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return
        if (expression.objectDeclaration.hasSingleOverriddenMethod()) {
            val superType = expression.singleSuperType() ?: return
            if (superType.isSamInterface) {
                report(CodeSmell(issue, Entity.from(expression), issue.description))
            }
        }
    }
}
