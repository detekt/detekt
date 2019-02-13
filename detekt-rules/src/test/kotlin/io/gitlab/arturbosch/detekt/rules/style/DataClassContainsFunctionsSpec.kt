package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Ivan Balaksha
 */

class DataClassContainsFunctionsSpec : Spek({
    val subject by memoized { DataClassContainsFunctions() }

    describe("DataClassContainsFunctions rule") {

        val path = Case.DataClassContainsFunctionsPositive.path()

        it("reports valid data class w/o conversion function") {
            assertThat(subject.lint(path)).hasSize(3)
        }

        it("reports valid data class w/ conversion function") {
            val config = TestConfig(mapOf(DataClassContainsFunctions.CONVERSION_FUNCTION_PREFIX to "to"))
            val rule = DataClassContainsFunctions(config)
            assertThat(rule.lint(path)).hasSize(2)
        }

        it("does not report data class w/o conversion function") {
            assertThat(subject.lint(Case.DataClassContainsFunctionsNegative.path())).hasSize(0)
        }
    }
})
