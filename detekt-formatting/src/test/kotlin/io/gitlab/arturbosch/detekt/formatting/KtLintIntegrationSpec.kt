package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class KtLintIntegrationSpec : Spek({

    describe("tests integration of formatting") {

        it("should work like KtLint") {
            val fileBefore = loadFile("integration/before.kt")
            val expected = loadFileContent("integration/after.kt")

            val ruleSet = loadRuleSet<FormattingProvider>(
                    TestConfig(mapOf("autoCorrect" to "true")))
            val findings = ruleSet.accept(fileBefore)

            assertThat(findings).isNotEmpty
            assertThat(fileBefore.text).isEqualTo(expected)
        }
    }
})
