package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isLateinit
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isObjectLiteral
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.jetbrains.kotlin.util.containingNonLocalDeclaration

/**
 * Reports var declarations (both local variables and private class properties) that could be val,
 * as they are not re-assigned. Val declarations are assign-once (read-only), which makes understanding
 * the current state easier.
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
@Alias("CanBeVal")
class VarCouldBeVal(config: Config) :
    Rule(
        config,
        "Var declaration could be val."
    ),
    RequiresAnalysisApi {

    @Configuration("Whether to ignore uninitialized lateinit vars")
    private val ignoreLateinitVar: Boolean by config(defaultValue = false)

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        val assignmentVisitor = AssignmentVisitor(ignoreLateinitVar)
        file.accept(assignmentVisitor)

        analyze(file) {
            assignmentVisitor.getNonReAssignedDeclarations().forEach {
                report(
                    Finding(
                        Entity.from(it),
                        "Variable '${it.nameAsSafeName.identifier}' could be val."
                    )
                )
            }
        }
    }

    @Suppress("TooManyFunctions")
    private class AssignmentVisitor(private val ignoreLateinitVar: Boolean) : DetektVisitor() {

        private val declarationCandidates = mutableSetOf<KtNamedDeclaration>()
        private val assignments = mutableMapOf<String, MutableSet<KtExpression>>()
        private val escapeCandidates = mutableMapOf<KaSymbol, List<KtProperty>>()

        context(session: KaSession)
        fun getNonReAssignedDeclarations(): List<KtNamedDeclaration> =
            declarationCandidates.filterNot { it.hasAssignments() }

        context(session: KaSession)
        private fun KtNamedDeclaration.hasAssignments(): Boolean {
            val declarationName = nameAsSafeName.toString()
            val assignments = assignments[declarationName]
            if (assignments.isNullOrEmpty()) return false
            with(session) {
                val declarationSymbol = symbol
                return assignments.any {
                    it.resolveToCall()?.singleVariableAccessCall()?.symbol == declarationSymbol
                }
            }
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            // The super() call should be first in the function so that any properties
            // declared in potential object literals can be evaluated.
            super.visitNamedFunction(function)
            analyze(function) {
                function.initializer?.let { evaluateReturnExpression(it) }
            }
        }

        override fun visitProperty(property: KtProperty) {
            super.visitProperty(property)
            if (property.isDeclarationCandidate()) {
                declarationCandidates.add(property)
            }

            // Check for whether the initializer contains an object literal.
            val initializer = property.initializer
            if (initializer != null) {
                analyze(property) {
                    evaluateAssignmentExpression(property.symbol, initializer)
                }
            }
        }

        override fun visitUnaryExpression(expression: KtUnaryExpression) {
            super.visitUnaryExpression(expression)
            if (expression.operationToken in unaryAssignmentOperators) {
                expression.baseExpression?.let(::visitAssignment)
            }
        }

        override fun visitBinaryExpression(expression: KtBinaryExpression) {
            super.visitBinaryExpression(expression)
            if (expression.operationToken in KtTokens.ALL_ASSIGNMENTS) {
                expression.left?.let(::visitAssignment)

                // Check for whether the assignment contains an object literal.
                val expressionRight = expression.right
                if (expressionRight != null) {
                    analyze(expression) {
                        val symbol = (expression.left as? KtNameReferenceExpression)?.mainReference?.resolveToSymbol()
                        if (symbol != null) {
                            evaluateAssignmentExpression(symbol, expressionRight)
                        }
                    }
                }
            }
        }

        override fun visitReturnExpression(expression: KtReturnExpression) {
            super.visitReturnExpression(expression)
            analyze(expression) {
                expression.returnedExpression?.let { evaluateReturnExpression(it) }
            }
        }

        private fun evaluateAssignmentExpression(symbol: KaSymbol, rightExpression: KtExpression) {
            when (rightExpression) {
                is KtObjectLiteralExpression -> {
                    escapeCandidates[symbol] = rightExpression.collectDescendantsOfType {
                        it.isEscapeCandidate()
                    }
                }
                is KtIfExpression -> {
                    rightExpression.then?.let { evaluateAssignmentExpression(symbol, it) }
                    rightExpression.`else`?.let { evaluateAssignmentExpression(symbol, it) }
                }
                is KtBlockExpression -> {
                    rightExpression.lastBlockStatementOrThis()
                        .takeIf { it != rightExpression }
                        ?.let { evaluateAssignmentExpression(symbol, it) }
                }
            }
        }

        private fun KaSession.evaluateReturnExpression(returnedExpression: KtExpression) {
            when (returnedExpression) {
                is KtObjectLiteralExpression -> {
                    returnedExpression.collectDescendantsOfType<KtProperty> {
                        it.isEscapeCandidate()
                    }.forEach(declarationCandidates::remove)
                }
                is KtNameReferenceExpression -> {
                    returnedExpression.mainReference.resolveToSymbol()?.let {
                        escapeCandidates[it]?.forEach(declarationCandidates::remove)
                    }
                }
                is KtIfExpression -> {
                    returnedExpression.then?.let { evaluateReturnExpression(it) }
                    returnedExpression.`else`?.let { evaluateReturnExpression(it) }
                }
                is KtBlockExpression -> {
                    returnedExpression.lastBlockStatementOrThis()
                        .takeIf { it != returnedExpression }
                        ?.let { evaluateReturnExpression(it) }
                }
            }
        }

        private fun KtProperty.isDeclarationCandidate(): Boolean =
            when {
                !isVar || isOverride() || (ignoreLateinitVar && isLateinit()) -> false
                isLocal || isPrivate() -> true
                else -> {
                    // Check for whether property belongs to an anonymous object
                    // defined in a function.
                    containingClassOrObject
                        ?.takeIf { it.isObjectLiteral() }
                        ?.containingNonLocalDeclaration() != null
                }
            }

        private fun KtProperty.isEscapeCandidate(): Boolean =
            !isPrivate() && (containingClassOrObject as? KtObjectDeclaration)?.isObjectLiteral() == true

        private fun visitAssignment(assignedExpression: KtExpression) {
            val name = if (assignedExpression is KtQualifiedExpression) {
                assignedExpression.selectorExpression
            } else {
                assignedExpression
            }?.text ?: return
            assignments.getOrPut(name) { mutableSetOf() }.add(assignedExpression)
        }
    }

    private companion object {
        private val unaryAssignmentOperators = setOf(KtTokens.MINUSMINUS, KtTokens.PLUSPLUS)
    }
}
