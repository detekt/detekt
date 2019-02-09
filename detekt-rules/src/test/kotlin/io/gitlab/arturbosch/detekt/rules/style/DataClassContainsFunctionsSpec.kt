package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Ivan Balaksha
 */

class DataClassContainsFunctionsSpec : SubjectSpek<DataClassContainsFunctions>({
    subject { DataClassContainsFunctions() }

    given("several data classes") {

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
