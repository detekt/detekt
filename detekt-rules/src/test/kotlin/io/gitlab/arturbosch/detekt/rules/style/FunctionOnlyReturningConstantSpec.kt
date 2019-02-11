package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FunctionOnlyReturningConstantSpec : Spek({
    val subject by memoized { FunctionOnlyReturningConstant() }

    describe("FunctionOnlyReturningConstant rule - positive cases") {

        val path = Case.FunctionReturningConstantPositive.path()

        it("reports functions which return constants") {
            assertThat(subject.lint(path)).hasSize(6)
        }

        it("reports overridden functions which return constants") {
            val config = TestConfig(mapOf("ignoreOverridableFunction" to "false"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(path)).hasSize(9)
        }

        it("does not report excluded function which returns a constant") {
            val code = "fun f() = 1"
            val config = TestConfig(mapOf("excludedFunctions" to "f"))
            val rule = FunctionOnlyReturningConstant(config)
            assertThat(rule.lint(code)).hasSize(0)
        }
    }

    describe("FunctionOnlyReturningConstant rule - negative cases") {

        it("does not report functions which do not return constants") {
            val path = Case.FunctionReturningConstantNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
