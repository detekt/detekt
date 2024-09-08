package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.metrics.CyclomaticComplexity
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Complex methods are hard to understand and read. It might not be obvious what side-effects a complex method has.
 * Prefer splitting up complex methods into smaller methods that are in turn easier to understand.
 * Smaller methods can also be named much clearer which leads to improved readability of the code.
 *
 * This rule uses McCabe's Cyclomatic Complexity (MCC) metric to measure the number of
 * linearly independent paths through a function's source code (https://www.ndepend.com/docs/code-metrics#CC).
 * The higher the number of independent paths, the more complex a method is.
 * Complex methods use too many of the following statements.
 * Each one of them adds one to the complexity count.
 *
 * - __Conditional statements__ - `if`, `else if`, `when`
 * - __Jump statements__ - `continue`, `break`
 * - __Loops__ - `for`, `while`, `do-while`, `forEach`
 * - __Operators__ `&&`, `||`, `?:`
 * - __Exceptions__ - `catch`, `use`
 * - __Scope Functions__ - `let`, `run`, `with`, `apply`, and `also` ->
 *  [Reference](https://kotlinlang.org/docs/scope-functions.html)
 */
@ActiveByDefault(since = "1.0.0")
class CyclomaticComplexMethod(config: Config) : Rule(
    config,
    "Prefer splitting up complex methods into smaller, easier to test methods."
) {

    @Configuration("The maximum allowed McCabe's Cyclomatic Complexity (MCC) for a method.")
    private val allowedComplexity: Int by config(defaultValue = 14)

    @Configuration("Ignores a complex method if it only contains a single when expression.")
    private val ignoreSingleWhenExpression: Boolean by config(false)

    @Configuration("Whether to ignore simple (braceless) when entries.")
    private val ignoreSimpleWhenEntries: Boolean by config(false)

    @Configuration("Whether to ignore functions which are often used instead of an `if` or `for` statement.")
    private val ignoreNestingFunctions: Boolean by config(false)

    @Configuration("Whether to ignore local functions and count them as one")
    private val ignoreLocalFunctions: Boolean by config(false)

    @Configuration("Comma separated list of function names which add complexity.")
    private val nestingFunctions: Set<String> by config(DEFAULT_NESTING_FUNCTIONS) { it.toSet() }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (ignoreSingleWhenExpression && hasSingleWhenExpression(function.bodyExpression)) {
            return
        }

        val complexity = CyclomaticComplexity.calculate(function) {
            this.ignoreSimpleWhenEntries = this@CyclomaticComplexMethod.ignoreSimpleWhenEntries
            this.ignoreNestingFunctions = this@CyclomaticComplexMethod.ignoreNestingFunctions
            this.ignoreLocalFunctions = this@CyclomaticComplexMethod.ignoreLocalFunctions
            this.nestingFunctions = this@CyclomaticComplexMethod.nestingFunctions
        }

        if (complexity > allowedComplexity) {
            report(
                CodeSmell(
                    Entity.atName(function),
                    "The function ${function.nameAsSafeName} appears to be too complex " +
                        "based on Cyclomatic Complexity (complexity: $complexity). " +
                        "The maximum allowed complexity for methods is set to '$allowedComplexity'"
                )
            )
        }
    }

    private fun hasSingleWhenExpression(bodyExpression: KtExpression?): Boolean = when {
        bodyExpression is KtBlockExpression && bodyExpression.statements.size == 1 -> {
            val statement = bodyExpression.statements.single()
            statement is KtWhenExpression || statement.returnsWhenExpression()
        }
        // the case where function-expression syntax is used: `fun test() = when { ... }`
        bodyExpression is KtWhenExpression -> true
        else -> false
    }

    private fun KtExpression.returnsWhenExpression() =
        this is KtReturnExpression && this.returnedExpression is KtWhenExpression

    companion object {
        val DEFAULT_NESTING_FUNCTIONS = listOf(
            "also",
            "apply",
            "forEach",
            "isNotNull",
            "ifNull",
            "let",
            "run",
            "use",
            "with",
        )
    }
}
