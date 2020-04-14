package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NewLineAtEndOfFileSpec : Spek({

    val subject by memoized { NewLineAtEndOfFile() }

    describe("NewLineAtEndOfFile rule") {

        it("should not flag a kt file containing new line at the end") {
            val code = "class Test\n\n" // we need double '\n' because .lint() applies .trimIndent() which removes one
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should flag a kt file not containing new line at the end") {
            val code = "class Test"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should not flag an empty kt file") {
            val code = ""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
