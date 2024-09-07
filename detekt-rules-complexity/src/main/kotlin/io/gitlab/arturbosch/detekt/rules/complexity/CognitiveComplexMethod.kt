package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.metrics.CognitiveComplexity
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Complex methods are hard to understand and read. It might not be obvious what side-effects a complex method has.
 * Prefer splitting up complex methods into smaller methods that are in turn easier to understand.
 * Smaller methods can also be named much clearer which leads to improved readability of the code.
 *
 * This rule measures and restricts the complexity of the method through the [Cognitive Complexity metric of Sonasource](https://www.sonarsource.com/docs/CognitiveComplexity.pdf).
 * Which improves McCabe's Cyclomatic Complexity (see [CyclomaticComplexMethod](/docs/rules/complexity#cyclomaticcomplexmethod)) considering the programmer's mental model.
 *
 * Similar to cyclomatic complexity, it is a mathematical model that increases +1 complexity for flow control statements,
 * but increases additional complexity when the statements are deeply nested.
 *
 * The statements that increase the complexity or the nesting level are as follows.
 * - __Complexity Increments__ - `if`, `when`, `for`, `while`, `do while`, `catch`, `labeled break`, `labeled continue`, `labeled return`, `recursion call`, `&&`, `||`
 * - __Nesting Level Increments__ - `if`, `when`, `for`, `while`, `do while`, `catch`, `nested function`
 * - __Additional Complexity Increments by Nesting Level__ - `if`, `when`, `for`, `while`, `do while`, `catch`
 */
class CognitiveComplexMethod(config: Config) : Rule(
    config,
    "Prefer splitting up complex methods into smaller, easier to understand methods."
) {

    @Configuration("Maximum Cognitive Complexity allowed for a method.")
    private val allowedComplexity: Int by config(defaultValue = 15)

    override fun visitNamedFunction(function: KtNamedFunction) {
        val complexity = CognitiveComplexity.calculate(function)

        if (complexity > allowedComplexity) {
            report(
                CodeSmell(
                    Entity.atName(function),
                    "The function ${function.nameAsSafeName} appears to be too complex " +
                        "based on Cognitive Complexity (complexity: $complexity). " +
                        "Defined maximum allowed complexity for methods is set to '$allowedComplexity'"
                )
            )
        }
    }
}
