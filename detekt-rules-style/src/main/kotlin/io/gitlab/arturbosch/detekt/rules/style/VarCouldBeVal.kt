package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isLateinit
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
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
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
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
    RequiresTypeResolution {
    @Configuration("Whether to ignore uninitialized lateinit vars")
    private val ignoreLateinitVar: Boolean by config(defaultValue = false)

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        val assignmentVisitor = AssignmentVisitor(bindingContext, ignoreLateinitVar)
        file.accept(assignmentVisitor)

        assignmentVisitor.getNonReAssignedDeclarations().forEach {
            report(
                CodeSmell(
                    Entity.from(it),
                    "Variable '${it.nameAsSafeName.identifier}' could be val."
                )
            )
        }
    }

    @Suppress("TooManyFunctions")
    private class AssignmentVisitor(
        private val bindingContext: BindingContext,
        private val ignoreLateinitVar: Boolean
    ) : DetektVisitor() {

        private val declarationCandidates = mutableSetOf<KtNamedDeclaration>()
        private val assignments = mutableMapOf<String, MutableSet<KtExpression>>()
        private val escapeCandidates = mutableMapOf<DeclarationDescriptor, List<KtProperty>>()

        fun getNonReAssignedDeclarations(): List<KtNamedDeclaration> =
            declarationCandidates.filterNot { it.hasAssignments() }

        private fun KtNamedDeclaration.hasAssignments(): Boolean {
            val declarationName = nameAsSafeName.toString()
            val assignments = assignments[declarationName]
            if (assignments.isNullOrEmpty()) return false
            val declarationDescriptor =
                bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, this]
            return assignments.any {
                it.getResolvedCall(bindingContext)?.resultingDescriptor?.original == declarationDescriptor ||
                    // inside an unknown types context? (example: with-statement with unknown type)
                    // (i.e, it can't be resolved if the assignment is from the context or from an outer variable)
                    it.getResolvedCall(bindingContext) == null
            }
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            // The super() call should be first in the function so that any properties
            // declared in potential object literals can be evaluated.
            super.visitNamedFunction(function)
            evaluateReturnExpression(function.initializer)
        }

        override fun visitProperty(property: KtProperty) {
            super.visitProperty(property)
            if (property.isDeclarationCandidate()) {
                declarationCandidates.add(property)
            }

            // Check for whether the initializer contains an object literal.
            val descriptor = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, property]
            val initializer = property.initializer
            if (descriptor != null && initializer != null) {
                evaluateAssignmentExpression(descriptor, initializer)
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
                val descriptor = (expression.left as? KtNameReferenceExpression)
                    ?.getResolvedCall(bindingContext)
                    ?.resultingDescriptor
                val expressionRight = expression.right
                if (descriptor != null && expressionRight != null) {
                    evaluateAssignmentExpression(descriptor, expressionRight)
                }
            }
        }

        override fun visitReturnExpression(expression: KtReturnExpression) {
            super.visitReturnExpression(expression)
            evaluateReturnExpression(expression.returnedExpression)
        }

        private fun evaluateAssignmentExpression(
            descriptor: DeclarationDescriptor,
            rightExpression: KtExpression,
        ) {
            when (rightExpression) {
                is KtObjectLiteralExpression -> {
                    escapeCandidates[descriptor] = rightExpression.collectDescendantsOfType {
                        it.isEscapeCandidate()
                    }
                }
                is KtIfExpression -> {
                    rightExpression.then?.let { evaluateAssignmentExpression(descriptor, it) }
                    rightExpression.`else`?.let { evaluateAssignmentExpression(descriptor, it) }
                }
                is KtBlockExpression -> {
                    rightExpression.lastBlockStatementOrThis()
                        .takeIf { it != rightExpression }
                        ?.let { evaluateAssignmentExpression(descriptor, it) }
                }
            }
        }

        private fun evaluateReturnExpression(returnedExpression: KtExpression?) {
            when (returnedExpression) {
                is KtObjectLiteralExpression -> {
                    returnedExpression.collectDescendantsOfType<KtProperty> {
                        it.isEscapeCandidate()
                    }.forEach(declarationCandidates::remove)
                }
                is KtNameReferenceExpression -> {
                    returnedExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.let { descriptor ->
                        escapeCandidates[descriptor]?.forEach(declarationCandidates::remove)
                    }
                }
                is KtIfExpression -> {
                    evaluateReturnExpression(returnedExpression.then)
                    evaluateReturnExpression(returnedExpression.`else`)
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
