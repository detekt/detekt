package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class DetektorTest : Spek({

    describe("detektor") {
        it("TestProvider gets excluded as RuleSet") {
            runDetektWithPattern("patterns/test-pattern.yml")
        }

        it("FindName rule gets excluded") {
            runDetektWithPattern("patterns/exclude-FindName.yml")
        }
    }
})

private fun runDetektWithPattern(patternToUse: String) {
    val instance = DetektFacade.create(ProcessingSettings(path,
            config = yamlConfig(patternToUse)),
            listOf(TestProvider(), TestProvider2()), emptyList())

    val run = instance.run()
    assertThat(run.findings["Test"]?.none { "Test.kt" in it.file } ?: true).isTrue()
}
