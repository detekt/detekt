package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class LongMethodSpec : Spek({

    val subject by memoized { LongMethod(threshold = 5) }

    describe("nested functions can be long") {

        it("should find two long methods") {
            val path = Case.LongMethodPositive.path()
            assertThat(subject.lint(path)).hasSize(2)
        }

        it("should not find too long methods") {
            val path = Case.LongMethodNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
