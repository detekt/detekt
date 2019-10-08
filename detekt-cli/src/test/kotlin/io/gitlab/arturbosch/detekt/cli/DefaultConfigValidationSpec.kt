package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.internal.verifyConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DefaultConfigValidationSpec : Spek({

    describe("default configuration is valid") {

        it("is valid comparing itself") {
            val baseline = yamlConfig("default-detekt-config.yml")
            val result = verifyConfig(baseline, baseline)
            assertThat(result).isEmpty()
        }
    }
})
