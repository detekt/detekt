package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

private val unaryAssignmentOperators = setOf(KtTokens.MINUSMINUS, KtTokens.PLUSPLUS)

/**
 * Reports var declarations (locally-scoped variables) that could be val, as they are not re-assigned.
 * Val declarations are assign-once (read-only), which makes understanding the current state easier.
 *
 * <noncompliant>
 * fun example() {
 *     var i = 1 // violation: this variable is never re-assigned
 *     val j = i + 1
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun example() {
 *     val i = 1
 *     val j = i + 1
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
@RequiresTypeResolution
class VarCouldBeVal(config: Config = Config.empty) : Rule(config) {

    override val defaultRuleIdAliases: Set<String> = setOf("CanBeVal")

    override val issue: Issue = Issue(
        "VarCouldBeVal",
        Severity.Maintainability,
        "Var declaration could be val.",
        Debt.FIVE_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (bindingContext == BindingContext.EMPTY || function.isSomehowNested()) {
            return
        }

        val assignmentVisitor = AssignmentVisitor(bindingContext)
        function.accept(assignmentVisitor)

        assignmentVisitor.getNonReAssignedDeclarations().forEach {
            report(CodeSmell(issue, Entity.from(it), "Variable '${it.nameAsSafeName.identifier}' could be val."))
        }
        super.visitNamedFunction(function)
    }

    private fun KtNamedFunction.isSomehowNested() =
        getStrictParentOfType<KtNamedFunction>() != null

    private class AssignmentVisitor(private val bindingContext: BindingContext) : DetektVisitor() {

        private val declarations = mutableSetOf<KtNamedDeclaration>()
        private val assignments = mutableMapOf<String, MutableSet<KtExpression>>()

        fun getNonReAssignedDeclarations(): List<KtNamedDeclaration> {
            return declarations.filterNot { it.hasAssignments() }
        }

        private fun KtNamedDeclaration.hasAssignments(): Boolean {
            val declarationName = nameAsSafeName.toString()
            val assignments = assignments[declarationName]
            if (assignments.isNullOrEmpty()) return false
            val declarationDescriptor = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, this]
            return assignments.any {
                it.getResolvedCall(bindingContext)?.resultingDescriptor == declarationDescriptor
            }
        }

        override fun visitProperty(property: KtProperty) {
            if (property.isVar) {
                declarations.add(property)
            }
            super.visitProperty(property)
        }

        override fun visitUnaryExpression(expression: KtUnaryExpression) {
            if (expression.operationToken in unaryAssignmentOperators) {
                visitAssignment(expression.baseExpression)
            }
            super.visitUnaryExpression(expression)
        }

        override fun visitBinaryExpression(expression: KtBinaryExpression) {
            if (expression.operationToken in KtTokens.ALL_ASSIGNMENTS) {
                visitAssignment(expression.left)
            }
            super.visitBinaryExpression(expression)
        }

        private fun visitAssignment(assignedExpression: KtExpression?) {
            if (assignedExpression == null) return
            val name = if (assignedExpression is KtQualifiedExpression) {
                assignedExpression.selectorExpression
            } else {
                assignedExpression
            }?.text ?: return
            assignments.getOrPut(name) { mutableSetOf() }.add(assignedExpression)
        }
    }
}
