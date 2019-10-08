package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class ConfigValidationSpec : Spek({

    describe("validate configuration file") {

        val baseline = yamlConfig("config_validation/baseline.yml")

        it("passes for same config test") {
            verifyConfig(baseline, baseline)
        }

        it("reports different rule set name") {
            val result = verifyConfig(
                yamlConfig("config_validation/other-ruleset-name.yml"),
                baseline
            )
            assertThat(result).contains(doesNotExistsMessage("code-smell"))
        }

        it("reports different nested property names") {
            val result = verifyConfig(
                yamlConfig("config_validation/other-nested-property-names.yml"),
                baseline
            )
            assertThat(result).contains(
                doesNotExistsMessage("complexity>LongLongMethod"),
                doesNotExistsMessage("complexity>LongParameterList>enabled"),
                doesNotExistsMessage("complexity>LargeClass>howMany"),
                doesNotExistsMessage("complexity>InnerMap>InnerKey"),
                doesNotExistsMessage("complexity>InnerMap>Inner2>nestedActive")
            )
        }

        it("reports different rule set name") {
            val result = verifyConfig(
                yamlConfig("config_validation/no-nested-config.yml"),
                baseline
            )
            assertThat(result).contains(
                nestedConfigExpectedMessage("complexity"),
                nestedConfigExpectedMessage("style>WildcardImport")
            )
        }

        it("reports unexpected nested configs") {
            // note that the baseline config is now the first argument
            val result = verifyConfig(baseline, yamlConfig("config_validation/no-value.yml"))
            assertThat(result).contains(
                unexpectedNestedConfigMessage("style"),
                unexpectedNestedConfigMessage("comments")
            )
        }
    }
})
