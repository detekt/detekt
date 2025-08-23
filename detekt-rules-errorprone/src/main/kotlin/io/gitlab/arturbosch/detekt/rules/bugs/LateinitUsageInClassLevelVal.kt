package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

/**
 * Reports cases where a lateinit var is assigned to a class-level val property.
 * This is dangerous because the lateinit var might not be initialized when the val is accessed,
 * leading to unexpected null values or `UninitializedPropertyAccessException`.
 *
 * Using a lateinit var to initialize a class-level val is problematic because the initialization
 * of the val happens during object construction, while the lateinit var might be initialized later.
 * This can lead to runtime exceptions or unexpected behavior.
 *
 * Instead of using a lateinit var directly in a val property initialization, consider using a
 * custom getter or initializing the lateinit var in all constructors and init blocks before
 * accessing it.
 *
 * <noncompliant>
 * open class BaseFoo {
 *     lateinit var unsafe: String
 * }
 *
 * class Foo : BaseFoo() {
 *     val someValue: String = unsafe
 * }
 * </noncompliant>
 *
 * <compliant>
 * open class BaseFoo {
 *     lateinit var unsafe: String
 * }
 *
 * class Foo : BaseFoo() {
 *     // Using a custom getter ensures the property is accessed only when needed
 *     val someValue: String
 *         get() = unsafe
 *
 *     // Or initialize the lateinit var in init block
 *     init {
 *         unsafe = "initialized"
 *     }
 * }
 * </compliant>
 */
class LateinitUsageInClassLevelVal(config: Config) :
    Rule(
        config,
        "Class-level val property initialized with a lateinit var. The lateinit var might not be initialized " +
            "when the val is accessed, leading to UninitializedPropertyAccessException."
    ),
    RequiresFullAnalysis {

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        checkClass(klass)
    }

    private fun checkClass(klass: KtClass) {
        klass.getProperties()
            .filter { !it.isVar && !it.isLocal && it.initializer != null }
            .forEach { property ->
                val initializer = property.initializer
                val lateinitReferences = mutableSetOf<KtReferenceExpression>()

                initializer?.accept(
                    object : KtTreeVisitorVoid() {

                        override fun visitReferenceExpression(expression: KtReferenceExpression) {
                            super.visitReferenceExpression(expression)
                            val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
                            val propertyDescriptor = resolvedCall.resultingDescriptor

                            // Check if the property is lateinit using the descriptor
                            // This is a brittle approach, but it's the best we can do without access to the original declaration.
                            // We're looking for "lateinit var" in the descriptor's string representation to be more specific.
                            val isLateinit = propertyDescriptor.toString().contains("lateinit var")

                            if (isLateinit) {
                                lateinitReferences.add(expression)
                            }
                        }
                    }
                )

                // Check if all lateinit vars are initialized in constructors and init blocks
                val uninitializedLateinitVars = lateinitReferences.filter { reference ->
                    val propertyName = reference.text.substringAfterLast('.')
                    !isLateinitVarInitializedInConstructorsAndInitBlocks(klass, propertyName)
                }

                if (uninitializedLateinitVars.isNotEmpty()) {
                    val message = "Property `${property.name}` is initialized with lateinit var that might not be " +
                        "initialized when accessed. Consider using a custom getter or initializing the lateinit var in all constructors."
                    report(Finding(Entity.from(property), message))
                }
            }
    }

    private fun isLateinitVarInitializedInConstructorsAndInitBlocks(klass: KtClass, propertyName: String): Boolean {
        // Get all constructors and init blocks
        val constructors = klass.declarations.filterIsInstance<KtConstructor<*>>()
        val initBlocks = klass.declarations.filterIsInstance<KtClassInitializer>()

        // If there are no constructors or init blocks, the lateinit var is not initialized
        if (constructors.isEmpty() && initBlocks.isEmpty()) {
            return false
        }

        // Check if the lateinit var is initialized in all constructors
        val allConstructorsInitializeLateinitVar = constructors.all { constructor ->
            isPropertyAssignedInBlock(constructor.bodyExpression, propertyName)
        }

        // Check if the lateinit var is initialized in all init blocks
        val allInitBlocksInitializeLateinitVar = initBlocks.all { initBlock ->
            isPropertyAssignedInBlock(initBlock.body, propertyName)
        }

        // The lateinit var is considered initialized if it's initialized in all constructors and init blocks
        return (constructors.isEmpty() || allConstructorsInitializeLateinitVar) &&
            (initBlocks.isEmpty() || allInitBlocksInitializeLateinitVar)
    }

    private fun isPropertyAssignedInBlock(block: KtExpression?, propertyName: String): Boolean {
        if (block == null) {
            return false
        }

        // Find all binary expressions that assign to the property
        val assignments = block.collectDescendantsOfType<KtBinaryExpression> { binaryExpression ->
            binaryExpression.operationToken == KtTokens.EQ &&
                (binaryExpression.left as? KtNameReferenceExpression)?.getReferencedName() == propertyName
        }

        // Check for direct assignments (not inside any conditional blocks)
        val directAssignments = assignments.filter { assignment ->
            assignment == block || assignment.parent == block
        }

        // Analyze conditional blocks to check if all execution paths initialize the property
        return directAssignments.isNotEmpty() || areAllPathsInitialized(block, propertyName)
    }

    private fun areAllPathsInitialized(block: KtExpression, propertyName: String): Boolean {
        // Analyze if-expressions
        val ifExpressionsInitialized = block.collectDescendantsOfType<KtIfExpression>().any { ifExpression ->
            ifExpression.parent == block &&
                isPropertyAssignedInIfExpression(ifExpression, propertyName)
        }

        // Analyze when-expressions
        val whenExpressionsInitialized = block.collectDescendantsOfType<KtWhenExpression>().any { whenExpression ->
            whenExpression.parent == block &&
                isPropertyAssignedInWhenExpression(whenExpression, propertyName)
        }

        // Analyze try-expressions
        val tryExpressionsInitialized = block.collectDescendantsOfType<KtTryExpression>().any { tryExpression ->
            tryExpression.parent == block &&
                isPropertyAssignedInTryExpression(tryExpression, propertyName)
        }

        return ifExpressionsInitialized || whenExpressionsInitialized || tryExpressionsInitialized
    }

    private fun isPropertyAssignedInIfExpression(ifExpression: KtIfExpression, propertyName: String): Boolean {
        val thenBranch = ifExpression.then
        val elseBranch = ifExpression.`else`

        // If there's no else branch, the property is not guaranteed to be assigned
        if (elseBranch == null) {
            return false
        }

        // Check if both branches assign the property
        val thenAssigns = isPropertyAssignedInBlock(thenBranch, propertyName)
        val elseAssigns = isPropertyAssignedInBlock(elseBranch, propertyName)

        return thenAssigns && elseAssigns
    }

    private fun isPropertyAssignedInWhenExpression(whenExpression: KtWhenExpression, propertyName: String): Boolean {
        val entries = whenExpression.entries

        // Check if there's an else branch (entry with no condition)
        // Check if all entries assign the property
        return entries.any { it.isElse } &&
            entries.all { entry ->
                isPropertyAssignedInBlock(entry.expression, propertyName)
            }
    }

    private fun isPropertyAssignedInTryExpression(tryExpression: KtTryExpression, propertyName: String): Boolean {
        val tryBlock = tryExpression.tryBlock
        val catchClauses = tryExpression.catchClauses
        val finallyBlock = tryExpression.finallyBlock

        // Check if the try block assigns the property
        val tryBlockAssigns = isPropertyAssignedInBlock(tryBlock, propertyName)

        // If there's a finally block that assigns the property, it's guaranteed to be assigned
        if (finallyBlock != null && isPropertyAssignedInBlock(finallyBlock.finalExpression, propertyName)) {
            return true
        }
        // If the try block assigns the property, check if all catch clauses also assign it
        return catchClauses.all { catchClause ->
            isPropertyAssignedInBlock(catchClause.catchBody, propertyName)
        } &&
            tryBlockAssigns
    }
}
