package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author schalkms
 */
class NewLineAtEndOfFileSpec : Spek({
    val subject by memoized { NewLineAtEndOfFile() }

    describe("NewLineAtEndOfFile rule") {

        it("should not flag a kt file containing new space at end") {
            assertThat(subject.lint(Case.NewLineAtEndOfFile.path())).hasSize(0)
        }

        it("should flag a kt file not containing new space at end") {
            assertThat(subject.compileAndLint("class Test")).hasSize(1)
        }

        it("should not flag an empty kt file") {
            assertThat(subject.lint(Case.EmptyKtFile.path())).hasSize(0)
        }
    }
})
