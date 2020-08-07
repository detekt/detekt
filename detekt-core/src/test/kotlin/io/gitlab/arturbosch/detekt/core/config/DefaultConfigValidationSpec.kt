package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.internal.validateConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DefaultConfigValidationSpec : Spek({

    describe("default configuration is valid") {

        val baseline by memoized { yamlConfig("default-detekt-config.yml") }

        it("is valid comparing itself") {
            assertThat(validateConfig(baseline, baseline)).isEmpty()
        }

        it("does not flag common known config sub sections") {
            assertThat(validateConfig(yamlConfig("common_known_sections.yml"), baseline)).isEmpty()
        }
    }
})
