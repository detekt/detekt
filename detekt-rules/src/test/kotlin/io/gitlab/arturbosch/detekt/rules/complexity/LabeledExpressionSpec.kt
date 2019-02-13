package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Ivan Balaksha
 * @author schalkms
 */
class LabeledExpressionSpec : Spek({

    val subject by memoized { LabeledExpression() }

    describe("LabeledExpression rule") {

        it("reports these labels") {
            subject.lint(Case.LabeledExpressionPositive.path())
            assertThat(subject.findings).hasSize(10)
        }

        it("does not report these labels") {
            subject.lint(Case.LabeledExpressionNegative.path())
            assertThat(subject.findings).isEmpty()
        }

        it("does not report excluded label") {
            val code = """fun f() {
    			loop@ for (i in 1..5) {}
    		"""
            val config = TestConfig(mapOf(LabeledExpression.IGNORED_LABELS to "loop"))
            val findings = LabeledExpression(config).lint(code)
            assertThat(findings).isEmpty()
        }
    }
})
