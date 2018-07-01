package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtUnaryExpression

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
 *
 * @author marianosimone
 */
class VarCouldBeVal(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("VarCouldBeVal",
			Severity.Maintainability,
			"Var declaration could be val.",
			Debt.FIVE_MINS,
			aliases = setOf("CanBeVal"))

	override fun visitNamedFunction(function: KtNamedFunction) {
		val assignmentVisitor = AssignmentVisitor()
		function.accept(assignmentVisitor)

		assignmentVisitor.getNonReAssignedDeclarations().forEach {
			report(CodeSmell(issue, Entity.from(it), "Variable '${it.nameAsSafeName.identifier}' could be val."))
		}
		super.visitNamedFunction(function)
	}

	private class AssignmentVisitor : DetektVisitor() {

		private val declarations = mutableSetOf<KtNamedDeclaration>()
		// an easy way to find declarations when walking up the contexts of an assignment
		private val contextsByDeclarationName = mutableMapOf<String, MutableSet<PsiElement>>()
		private val assignments = mutableMapOf<String, MutableSet<PsiElement>>()

		fun getNonReAssignedDeclarations(): List<KtNamedDeclaration> {
			return declarations.filter { declaration ->
				assignments[declaration.nameAsSafeName.identifier]
						?.let { declaration.parent !in it }
						?: true
			}
		}

		override fun visitProperty(property: KtProperty) {
			if (property.isVar) {
				declarations.add(property)
				val contextsForName = contextsByDeclarationName.getOrPut(property.nameAsSafeName.identifier) { mutableSetOf() }
				contextsForName.add(property.parent)
			}
			super.visitProperty(property)
		}

		override fun visitUnaryExpression(expression: KtUnaryExpression) {
			if (expression.operationToken in unaryAssignmentOperators) {
				expression.baseExpression?.run {
					visitAssignment(text, expression.parent)
				}
			}
			super.visitUnaryExpression(expression)
		}

		override fun visitBinaryExpression(expression: KtBinaryExpression) {
			if (expression.operationToken in KtTokens.ALL_ASSIGNMENTS) {
				visitAssignment(expression.firstChild.text, expression.parent)
			}
			super.visitBinaryExpression(expression)
		}

		private fun visitAssignment(assignedName: String, context: PsiElement) {
			val potentialContexts = contextsByDeclarationName[assignedName]
			if (potentialContexts != null) {
				val actualContextChain = generateSequence(context) { it.parent }
				val actualContext = actualContextChain.firstOrNull { it in potentialContexts }
				if (actualContext != null) {
					val nameAssignments = assignments.getOrPut(assignedName) { mutableSetOf() }
					nameAssignments.add(actualContext)
				}
			}
		}
	}
}
