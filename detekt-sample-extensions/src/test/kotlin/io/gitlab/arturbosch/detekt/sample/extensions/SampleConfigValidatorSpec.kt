package io.gitlab.arturbosch.detekt.sample.extensions

import dev.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SampleConfigValidatorSpec {

    @Test
    fun `it warns if active property is not a boolean`() {
        val config = yamlConfigFromContent(
            """
                sample:
                  TooManyFunctions:
                    active: 1
            """.trimIndent()
        )

        val warnings = SampleConfigValidator().validate(config)

        assertThat(warnings)
            .hasSize(1)
            .extracting("message")
            .contains("'active' property must be of type boolean.")
    }
}
