package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.load.java.sam.JavaSingleAbstractMethodUtils
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitClassReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.KotlinType

/**
 * An anonymous object that does nothing other than the implementation of a single method
 * can be used as a lambda.
 *
 * See [SAM conversions](https://kotlinlang.org/docs/java-interop.html#sam-conversions),
 * [Functional (SAM) interfaces](https://kotlinlang.org/docs/fun-interfaces.html)
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
@ActiveByDefault(since = "1.21.0")
class ObjectLiteralToLambda(config: Config) :
    Rule(
        config,
        "Report object literals that can be changed to lambdas."
    ),
    RequiresTypeResolution {
    private val KotlinType.couldBeSamInterface
        get() = JavaSingleAbstractMethodUtils.isSamType(this)

    private fun KotlinType.singleSuperTypeOrNull(): KotlinType? =
        constructor.supertypes.singleOrNull()

    private fun KtObjectDeclaration.singleNamedMethodOrNull(): KtNamedFunction? =
        declarations.singleOrNull() as? KtNamedFunction

    private fun KtExpression.containsThisReference(descriptor: DeclarationDescriptor) =
        anyDescendantOfType<KtThisExpression> { thisReference ->
            bindingContext[BindingContext.REFERENCE_TARGET, thisReference.instanceReference] == descriptor
        }

    private fun KtExpression.containsOwnMethodCall(descriptor: DeclarationDescriptor) =
        anyDescendantOfType<KtExpression> {
            it.getResolvedCall(bindingContext)?.let { resolvedCall ->
                resolvedCall.dispatchReceiver.isImplicitClassFor(descriptor) ||
                    resolvedCall.extensionReceiver.isImplicitClassFor(descriptor)
            } == true
        }

    private fun ReceiverValue?.isImplicitClassFor(descriptor: DeclarationDescriptor) =
        this is ImplicitClassReceiver && classDescriptor == descriptor

    private fun KtExpression.containsMethodOf(declaration: KtObjectDeclaration): Boolean {
        val objectDescriptor = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, declaration]
            ?: return false

        return containsThisReference(objectDescriptor) ||
            containsOwnMethodCall(objectDescriptor)
    }

    private fun KtObjectDeclaration.hasConvertibleMethod(): Boolean {
        val singleNamedMethod = singleNamedMethodOrNull()
        val functionBody = singleNamedMethod?.bodyExpression ?: return false

        return singleNamedMethod.isOverride() &&
            !functionBody.containsMethodOf(this)
    }

    override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
        super.visitObjectLiteralExpression(expression)
        val declaration = expression.objectDeclaration

        if (
            declaration.name == null &&
            bindingContext.getType(expression)
                ?.singleSuperTypeOrNull()
                ?.couldBeSamInterface == true &&
            declaration.hasConvertibleMethod()
        ) {
            report(CodeSmell(Entity.from(expression), description))
        }
    }
}
