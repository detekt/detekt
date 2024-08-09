package io.gitlab.arturbosch.detekt.generator.printer.defaultconfig

import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConfigPrinterSpec {

    private val ruleSetPage = createRuleSetPage()
    private val yamlString = ConfigPrinter.print(listOf(ruleSetPage))

    @Test
    fun `prints the rule set in the correct yaml format`() {
        val expectedRulePart = """
            style:
              active: true
              rulesetconfig1: true
              rulesetconfig2:
                - 'foo'
                - 'bar'
              rulesetconfig3:
                - 'first'
                - 'se*cond'
              MagicNumber:
                active: true
                aliases: ['alias1', 'alias2']
                excludes: ['**/test/**', '**/androidTest/**', '**/commonTest/**', '**/jvmTest/**', '**/androidUnitTest/**', '**/androidInstrumentedTest/**', '**/jsTest/**', '**/iosTest/**', '**/*.kts']
                conf1: 'foo'
                conf3:
                  - 'a'
                  - 'b'
                conf5: 120
              EqualsNull:
                active: false
              NoUnitKeyword:
                active: true
                autoCorrect: true

        """.trimIndent()

        assertThat(yamlString).endsWith(expectedRulePart)
    }

    @Test
    fun `prints default config configuration`() {
        assertThat(yamlString).contains("config:")
    }

    @Test
    fun `prints default processor configuration`() {
        assertThat(yamlString).contains("processors:")
    }

    @Test
    fun `prints default report configuration`() {
        assertThat(yamlString).contains("output-reports:")
        assertThat(yamlString).contains("console-reports:")
    }

    @Test
    fun `omits deprecated ruleset properties`() {
        assertThat(yamlString).doesNotContain("deprecatedSimpleConfig")
        assertThat(yamlString).doesNotContain("deprecatedListConfig")
    }

    @Test
    fun `omits deprecated rule properties`() {
        assertThat(yamlString).doesNotContain("conf2")
        assertThat(yamlString).doesNotContain("conf4")
    }
}
