package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.isNonNullCheck
import org.jetbrains.kotlin.backend.common.peek
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
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
 *
 * class A(private var a: Int?) {
 *   inner class B {
 *     fun foo() {
 *       if (a != null) {
 *         println(a)
 *       }
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
 *
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
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Checking nullability on a mutable property is not useful because the " +
            "property may be set to null afterwards.",
        Debt.TEN_MINS
    )

    override fun visitKtFile(file: KtFile) {
        if (bindingContext == BindingContext.EMPTY) return
        NullCheckVisitor().visitKtFile(file)
    }

    private inner class NullCheckVisitor : DetektVisitor() {
        private val mutableProperties = mutableSetOf<FqName>()
        private val candidateProperties = mutableMapOf<FqName, ArrayDeque<KtIfExpression>>()

        override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
            constructor.valueParameters.asSequence()
                .filter { it.isMutable }
                .mapNotNull { it.fqName }
                .forEach(mutableProperties::add)
            super.visitPrimaryConstructor(constructor)
        }

        override fun visitProperty(property: KtProperty) {
            if (property.isVar) property.fqName?.let(mutableProperties::add)
            super.visitProperty(property)
        }

        override fun visitIfExpression(expression: KtIfExpression) {
            val condition = expression.condition
            if (condition is KtBinaryExpression && condition.isNonNullCheck()) {
                // Determine which of the two sides of the condition is the null constant and use the other.
                if (condition.left is KtConstantExpression) {
                    condition.right as? KtNameReferenceExpression
                } else {
                    condition.left as? KtNameReferenceExpression
                }?.let { referenceExpression ->
                    referenceExpression.getResolvedCall(bindingContext)
                        ?.resultingDescriptor
                        ?.let {
                            it.fqNameOrNull()?.takeIf(mutableProperties::contains)
                        }
                }?.let { candidateFqName ->
                    // If a candidate mutable property is present, attach the current
                    // if-expression to it and proceed within the if-expression.
                    val ifExpressionStack = candidateProperties.getOrPut(candidateFqName) { ArrayDeque() }
                    ifExpressionStack.add(expression)
                    super.visitIfExpression(expression)
                    // Remove the if-expression after having iterated out of its code block.
                    ifExpressionStack.pop()
                    return
                }
            }
            super.visitIfExpression(expression)
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            val fqName = expression.getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.fqNameOrNull()
            // Don't check the reference expression if it's being invoked in the if-expression
            // where it's being null-checked.
            if (expression.parent !is KtBinaryExpression && fqName != null) {
                // If there's an if-expression attached to the candidate property, a null-checked
                // mutable property is being referenced.
                candidateProperties[fqName]?.peek()?.let { ifExpression ->
                    report(
                        CodeSmell(
                            issue,
                            Entity.from(ifExpression),
                            "Null-check is being called on a mutable property."
                        )
                    )
                }
            }
            super.visitReferenceExpression(expression)
        }
    }
}
