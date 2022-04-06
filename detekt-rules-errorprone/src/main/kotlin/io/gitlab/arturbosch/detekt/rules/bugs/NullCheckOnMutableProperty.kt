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
import io.gitlab.arturbosch.detekt.rules.isNullCheck
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
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
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
        super.visitKtFile(file)
        NullCheckVisitor().visitKtFile(file)
    }

    private inner class NullCheckVisitor : DetektVisitor() {
        private val mutableProperties = mutableSetOf<FqName>()
        private val candidateProperties = mutableMapOf<FqName, MutableList<KtIfExpression>>()

        override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
            super.visitPrimaryConstructor(constructor)
            constructor.valueParameters.asSequence()
                .filter { it.isMutable }
                .mapNotNull { it.fqName }
                .forEach(mutableProperties::add)
        }

        override fun visitProperty(property: KtProperty) {
            super.visitProperty(property)
            val fqName = property.fqName
            if (fqName != null && (property.isVar || property.getter != null)) {
                mutableProperties.add(fqName)
            }
        }

        override fun visitIfExpression(expression: KtIfExpression) {
            // Extract all possible null-checks within the if-expression.
            val nonNullChecks = (expression.condition as? KtBinaryExpression)
                ?.collectNonNullChecks()
                .orEmpty()

            val modifiedCandidateQueues = nonNullChecks.mapNotNull { nonNullCondition ->
                if (nonNullCondition.left is KtConstantExpression) {
                    nonNullCondition.right as? KtNameReferenceExpression
                } else {
                    nonNullCondition.left as? KtNameReferenceExpression
                }?.let { referenceExpression ->
                    referenceExpression.getResolvedCall(bindingContext)
                        ?.resultingDescriptor
                        ?.let {
                            it.fqNameOrNull()?.takeIf(mutableProperties::contains)
                        }
                }?.let { candidateFqName ->
                    // A candidate mutable property is present, so attach the current
                    // if-expression to it in the property candidates map.
                    candidateProperties.getOrPut(candidateFqName) { ArrayDeque() }.apply { add(expression) }
                }
            }
            // Visit descendant expressions to see whether candidate properties
            // identified in this if-expression are being referenced.
            super.visitIfExpression(expression)
            // Remove the if-expression after having iterated out of its code block.
            modifiedCandidateQueues.forEach { it.pop() }
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            super.visitReferenceExpression(expression)
            expression.getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.fqNameOrNull()
                ?.let { fqName ->
                    val expressionParent = expression.parent
                    // Don't check the reference expression if it's being invoked in the if-expression
                    // where it's being null-checked.
                    if (
                        expressionParent !is KtBinaryExpression ||
                        (!expressionParent.isNonNullCheck() && !expressionParent.isNullCheck())
                    ) {
                        // If there's an if-expression attached to the candidate property, a null-checked
                        // mutable property is being referenced.
                        candidateProperties[fqName]?.peek()?.let { ifExpression ->
                            report(
                                CodeSmell(
                                    issue,
                                    Entity.from(ifExpression),
                                    "Null-check is being called on mutable property '$fqName'."
                                )
                            )
                        }
                    }
                }
        }

        private fun KtBinaryExpression.collectNonNullChecks(): List<KtBinaryExpression> {
            return if (isNonNullCheck()) {
                listOf(this)
            } else {
                val nonNullChecks = mutableListOf<KtBinaryExpression>()
                (left as? KtBinaryExpression)?.let { nonNullChecks.addAll(it.collectNonNullChecks()) }
                (right as? KtBinaryExpression)?.let { nonNullChecks.addAll(it.collectNonNullChecks()) }
                nonNullChecks
            }
        }
    }
}
