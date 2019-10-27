package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.CompositeConfig
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class ConfigValidationSpec : Spek({

    describe("validate configuration file") {

        val baseline = yamlConfig("config_validation/baseline.yml")

        it("passes for same config test") {
            validateConfig(baseline, baseline)
        }

        it("reports different rule set name") {
            val result = validateConfig(
                yamlConfig("config_validation/other-ruleset-name.yml"),
                baseline
            )
            assertThat(result).contains(doesNotExistsMessage("code-smell"))
        }

        it("reports different nested property names") {
            val result = validateConfig(
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
            val result = validateConfig(
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
            val result = validateConfig(baseline, yamlConfig("config_validation/no-value.yml"))
            assertThat(result).contains(
                unexpectedNestedConfigMessage("style"),
                unexpectedNestedConfigMessage("comments")
            )
        }

        describe("validate composite configurations") {

            it("passes for same left, right and baseline config") {
                val result = validateConfig(CompositeConfig(baseline, baseline), baseline)
                assertThat(result).isEmpty()
            }

            it("passes for empty configs") {
                val result = validateConfig(CompositeConfig(Config.empty, Config.empty), baseline)
                assertThat(result).isEmpty()
            }

            it("finds accumulated errors") {
                val result = validateConfig(
                    CompositeConfig(
                        yamlConfig("config_validation/other-nested-property-names.yml"),
                        yamlConfig("config_validation/no-nested-config.yml")
                    ),
                    baseline
                )

                assertThat(result).contains(
                    nestedConfigExpectedMessage("complexity"),
                    nestedConfigExpectedMessage("style>WildcardImport"),
                    doesNotExistsMessage("complexity>LongLongMethod"),
                    doesNotExistsMessage("complexity>LongParameterList>enabled"),
                    doesNotExistsMessage("complexity>LargeClass>howMany"),
                    doesNotExistsMessage("complexity>InnerMap>InnerKey"),
                    doesNotExistsMessage("complexity>InnerMap>Inner2>nestedActive")
                )
            }
        }
    }
})
