package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class EmptyIfBlockSpec : Spek({

    val subject by memoized { EmptyIfBlock(Config.empty) }

    describe("EmptyIfBlock rule") {

        it("reports positive cases") {
            val path = Case.EmptyIfPositive.path()
            assertThat(subject.lint(path)).hasSize(4)
        }

        it("does not report negative cases") {
            val path = Case.EmptyIfNegative.path()
            assertThat(subject.lint(path)).hasSize(0)
        }
    }
})
