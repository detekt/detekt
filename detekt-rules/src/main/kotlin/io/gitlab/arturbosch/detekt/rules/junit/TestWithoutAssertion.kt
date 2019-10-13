package io.gitlab.arturbosch.detekt.rules.junit

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule report JUnit tests that do not have any assertions.
 * Assertions help to understand the purpose of the test.
 *
 */
class TestWithoutAssertion(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "A JUnit test should have at least one assertion",
        Debt.TEN_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        function.annotationEntries.find {
            it.text == TEST_ANNOTATION
        }?.let {
            val body = function.bodyExpression?.text
            if (body?.contains(ASSERTION_PATTERN, ignoreCase = true) == false) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(function),
                        issue.description
                    )
                )
            }
        }
    }

    companion object {
        private const val TEST_ANNOTATION = "@Test"
        private const val ASSERTION_PATTERN = "assert"
    }
}
