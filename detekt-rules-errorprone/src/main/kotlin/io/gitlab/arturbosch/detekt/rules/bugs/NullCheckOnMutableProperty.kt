package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.isNonNullCheck
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Reports null-checks on mutable properties, as these properties' value can be
 * changed - and thus make the null-check invalid - after the execution of the
 * if-statement.
 *
 * <noncompliant>
 * class A(private var a: Int?) {
 *   fun foo() {
 *     if (a != null) {
 *       println(2 + a!!)
 *     }
 *   }
 * }
 * </noncompliant>
 *
 * <compliant>
 * class A(private val a: Int?) {
 *   fun foo() {
 *     if (a != null) {
 *       println(2 + a)
 *     }
 *   }
 * }
 * </compliant>
 *
 * <compliant>
 * class A(private var a: Int?) {
 *   fun foo() {
 *     val a = a
 *     if (a != null) {
 *       println(2 + a)
 *     }
 *   }
 * }
 * </compliant>
 */

@RequiresTypeResolution
class NullCheckOnMutableProperty(config: Config) : Rule(config) {
    private val mutableProperties = mutableMapOf<String?, MutableSet<String>>()
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Checking nullability on a mutable property is not useful because the " +
            "property may be set to null afterwards.",
        Debt.TEN_MINS
    )

    override fun visitKtFile(file: KtFile) {
        if (bindingContext == BindingContext.EMPTY) return
        super.visitKtFile(file)
        mutableProperties.clear()
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        val containerName = constructor.getContainingClassOrObject().name
        constructor.valueParameters.asSequence()
            .filter { it.isMutable }
            .mapNotNull { it.name }
            .forEach { mutableProperties.getOrPut(containerName) { mutableSetOf() }.add(it) }
        super.visitPrimaryConstructor(constructor)
    }

    override fun visitProperty(property: KtProperty) {
        if (property.isVar) {
            property.name?.let { propName ->
                val containerName = property.containingClassOrObject?.name ?: "" // A root-level property
                mutableProperties.getOrPut(
                    containerName
                ) { mutableSetOf() }.add(propName)
            }
        }
        super.visitProperty(property)
    }

    override fun visitIfExpression(expression: KtIfExpression) {
        val condition = expression.condition
        if (condition is KtBinaryExpression && condition.isNonNullCheck()) {
            // Determine which of the two sides of the condition is the null constant and use the other.
            if (condition.left is KtConstantExpression) {
                condition.right
            } else {
                condition.left
            }?.let {
                // Only proceed with evaluating if the checked expression is for a variable.
                it as? KtNameReferenceExpression
            }?.let {
                evaluateNameReferenceExpression(expression, it)
            }
        }
        super.visitIfExpression(expression)
    }

    private fun evaluateNameReferenceExpression(
        expression: KtIfExpression,
        referenceExpression: KtNameReferenceExpression
    ) {
        val resolvedCall = referenceExpression.getResolvedCall(bindingContext) ?: return
        val containingDeclaration = resolvedCall.resultingDescriptor.containingDeclaration
        if (
            mutableProperties[containingDeclaration.fqNameOrNull()?.asString()]
                ?.contains(referenceExpression.text) == true
        ) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Null-check is being called on a mutable property."
                )
            )
        }
    }
}
