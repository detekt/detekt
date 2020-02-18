package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MaximumLineLengthSpec : Spek({

    val subject by memoized {
        val config = TestConfig(MaximumLineLength.MAX_LINE_LENGTH to "10")
        MaximumLineLength(config)
    }

    describe("MaximumLineLength rule") {

        describe("a single function") {

            val code = "fun f() { }"

            it("reports line which exceeds the threshold") {
                assertThat(subject.lint(code)).hasSize(1)
            }

            it("does not report line which does not exceed the threshold") {
                val config = TestConfig(MaximumLineLength.MAX_LINE_LENGTH to "11")
                assertThat(MaximumLineLength(config).lint(code)).isEmpty()
            }
        }

        it("does not report line which does not exceed the threshold") {
            val code = "val a = 1"
            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
