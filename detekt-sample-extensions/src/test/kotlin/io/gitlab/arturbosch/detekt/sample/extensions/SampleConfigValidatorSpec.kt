package io.gitlab.arturbosch.detekt.sample.extensions

import dev.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SampleConfigValidatorSpec {

    @Test
    fun `it warns if active property is not a boolean`() {
        val config = TestConfig(
            "sample" to (
                "TooManyFunctions" to (
                    "active" to 1
                    )
                )
        )

        val warnings = SampleConfigValidator().validate(config)

        assertThat(warnings)
            .hasSize(1)
            .extracting("message")
            .contains("'active' property must be of type boolean.")
    }
}
