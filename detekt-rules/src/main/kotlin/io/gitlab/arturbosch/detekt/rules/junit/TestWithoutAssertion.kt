package io.gitlab.arturbosch.detekt.rules.junit

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule report JUnit tests that do not have any assertions.
 * Assertions help to understand the purpose of the test.
 *
 * @configuration assertionPattern - assertion pattern (default: `'assert'`)
 */
class TestWithoutAssertion(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "A JUnit test should have at least one assertion",
        Debt.TEN_MINS
    )
    private val assertionPattern by LazyRegex(ASSERTION_PATTERN, "assert")

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val hasTestAnnotation = function.annotationEntries.any { it.text == TEST_ANNOTATION }
        if (hasTestAnnotation) {
            val functionBody = function.bodyExpression?.text ?: return
            if (!assertionPattern.containsMatchIn(functionBody)) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(function),
                        "JUnit test should contain at least one assertion matching $assertionPattern"
                    )
                )
            }
        }
    }

    companion object {
        private const val ASSERTION_PATTERN = "assertionPattern"
        private const val TEST_ANNOTATION = "@Test"
    }
}
