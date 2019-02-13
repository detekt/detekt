package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class LongParameterListSpec : Spek({

    val subject by memoized { LongParameterList() }

    describe("LongParameterList rule") {

        it("reports too long parameter list") {
            val code = "fun long(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) {}"
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not reports short parameter list") {
            val code = "fun long(a: Int, b: Int, c: Int, d: Int, e: Int) {}"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports too long parameter list event for parameters with defaults") {
            val code = "fun long(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int = 1) {}"
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report long parameter list if parameters with defaults should be ignored") {
            val config = TestConfig(mapOf(LongParameterList.IGNORE_DEFAULT_PARAMETERS to "true"))
            val rule = LongParameterList(config)
            val code = "fun long(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int = 1, g: Int = 2) {}"
            assertThat(rule.lint(code)).isEmpty()
        }
    }
})
