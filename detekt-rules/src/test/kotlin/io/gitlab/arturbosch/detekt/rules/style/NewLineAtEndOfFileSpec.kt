package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NewLineAtEndOfFileSpec : Spek({

    val subject by memoized { NewLineAtEndOfFile() }

    describe("NewLineAtEndOfFile rule") {

        it("should not flag a kt file containing new line at the end") {
            val file = compileContentForTest("class Test\n")
            assertThat(subject.lint(file)).isEmpty()
        }

        it("should flag a kt file not containing new line at the end") {
            val file = compileContentForTest("class Test")
            assertThat(subject.lint(file)).hasSize(1)
        }

        it("should not flag an empty kt file") {
            val file = compileContentForTest("")
            assertThat(subject.lint(file)).isEmpty()
        }
    }
})
