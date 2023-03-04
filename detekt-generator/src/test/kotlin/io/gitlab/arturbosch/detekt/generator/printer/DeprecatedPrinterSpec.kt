package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.util.createRuleSetPage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeprecatedPrinterSpec {

    @Test
    fun `prints the correct properties`() {
        val propertiesString = DeprecatedPrinter.print(listOf(createRuleSetPage()))
        val expectedPropertiesString = listOf(
            "style>MagicNumber>conf2=use conf1 instead",
            "style>MagicNumber>conf4=use conf3 instead",
            "style>DuplicateCaseInWhenExpression=is deprecated",
        )
            .plus(migratedRules())
            .sorted()
            .joinToString("\n", postfix = "\n")

        assertThat(propertiesString).isEqualTo(expectedPropertiesString)
    }
}
