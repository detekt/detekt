package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.internal.validateConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DefaultConfigValidationSpec : Spek({

    describe("default configuration is valid") {

        val baseline = yamlConfig("default-detekt-config.yml")

        it("is valid comparing itself") {
            assertThat(validateConfig(baseline, baseline)).isEmpty()
        }

        it("does not flag common known config sub sections") {
            assertThat(validateConfig(yamlConfig("configs/common_known_sections.yml"), baseline)).isEmpty()
        }
    }
})
