package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author schalkms
 */
class SwallowedExceptionSpec : Spek({
    val subject by memoized { SwallowedException() }

    describe("SwallowedException rule") {

        it("reports swallowed exceptions") {
            assertThat(subject.lint(Case.SwallowedExceptionPositive.path())).hasSize(5)
        }

        it("ignores given exception types in configuration") {
            val config = TestConfig(mapOf(SwallowedException.IGNORED_EXCEPTION_TYPES to "IOException"))
            val rule = SwallowedException(config)
            assertThat(rule.lint(Case.SwallowedExceptionPositive.path())).hasSize(4)
        }

        it("does not report thrown catch blocks") {
            assertThat(subject.lint(Case.SwallowedExceptionNegative.path())).hasSize(0)
        }
    }
})
