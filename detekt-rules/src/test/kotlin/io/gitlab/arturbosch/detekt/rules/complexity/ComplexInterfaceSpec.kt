package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ComplexInterfaceSpec : Spek({

    val subject by memoized { ComplexInterface(threshold = THRESHOLD) }

    describe("ComplexInterface rule") {

        val path = Case.ComplexInterfacePositive.path()

        it("reports interfaces which member size exceeds the threshold") {
            assertThat(subject.lint(path)).hasSize(2)
        }

        it("reports interfaces which member size exceeds the threshold including static declarations") {
            val config = TestConfig(mapOf(ComplexInterface.INCLUDE_STATIC_DECLARATIONS to "true"))
            val rule = ComplexInterface(config, threshold = THRESHOLD)
            assertThat(rule.lint(path)).hasSize(3)
        }

        it("does not report interfaces which member size is under the threshold") {
            assertThat(subject.lint(Case.ComplexInterfaceNegative.path())).hasSize(0)
        }
    }
})

private const val THRESHOLD = 4
