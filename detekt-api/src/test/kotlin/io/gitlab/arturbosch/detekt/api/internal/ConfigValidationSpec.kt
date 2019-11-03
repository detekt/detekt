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
            val result = validateConfig(baseline, baseline)
            assertThat(result).isEmpty()
        }

        it("passes for properties which may appear on rules but may be not present in default config") {
            val result = validateConfig(
                yamlConfig("config_validation/default-excluded-properties.yml"),
                baseline
            )
            assertThat(result).isEmpty()
        }

        it("reports different rule set name") {
            val result = validateConfig(
                yamlConfig("config_validation/other-ruleset-name.yml"),
                baseline
            )
            assertThat(result).contains(propertyDoesNotExists("code-smell"))
        }

        it("reports different nested property names") {
            val result = validateConfig(
                yamlConfig("config_validation/other-nested-property-names.yml"),
                baseline
            )
            assertThat(result).contains(
                propertyDoesNotExists("complexity>LongLongMethod"),
                propertyDoesNotExists("complexity>LongParameterList>enabled"),
                propertyDoesNotExists("complexity>LargeClass>howMany"),
                propertyDoesNotExists("complexity>InnerMap>InnerKey"),
                propertyDoesNotExists("complexity>InnerMap>Inner2>nestedActive")
            )
        }

        it("reports different rule set name") {
            val result = validateConfig(
                yamlConfig("config_validation/no-nested-config.yml"),
                baseline
            )
            assertThat(result).contains(
                nestedConfigurationExpected("complexity"),
                nestedConfigurationExpected("style>WildcardImport")
            )
        }

        it("reports unexpected nested configs") {
            // note that the baseline config is now the first argument
            val result = validateConfig(baseline, yamlConfig("config_validation/no-value.yml"))
            assertThat(result).contains(
                unexpectedNestedConfiguration("style"),
                unexpectedNestedConfiguration("comments")
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
                    nestedConfigurationExpected("complexity"),
                    nestedConfigurationExpected("style>WildcardImport"),
                    propertyDoesNotExists("complexity>LongLongMethod"),
                    propertyDoesNotExists("complexity>LongParameterList>enabled"),
                    propertyDoesNotExists("complexity>LargeClass>howMany"),
                    propertyDoesNotExists("complexity>InnerMap>InnerKey"),
                    propertyDoesNotExists("complexity>InnerMap>Inner2>nestedActive")
                )
            }
        }

        describe("configure additional exclude paths") {

            fun patterns(str: String) = CommaSeparatedPattern(str).mapToRegex()

            it("does not report any complexity properties") {
                val result = validateConfig(
                    yamlConfig("config_validation/other-nested-property-names.yml"),
                    baseline,
                    patterns("complexity")
                )
                assertThat(result).isEmpty()
            }

            it("does not report 'complexity>LargeClass>howMany'") {
                val result = validateConfig(
                    yamlConfig("config_validation/other-nested-property-names.yml"),
                    baseline,
                    patterns(".*>.*>howMany")
                )

                assertThat(result).contains(
                    propertyDoesNotExists("complexity>LongLongMethod"),
                    propertyDoesNotExists("complexity>LongParameterList>enabled"),
                    propertyDoesNotExists("complexity>InnerMap>InnerKey"),
                    propertyDoesNotExists("complexity>InnerMap>Inner2>nestedActive")
                )

                assertThat(result).doesNotContain(
                    propertyDoesNotExists("complexity>LargeClass>howMany")
                )
            }

            it("does not report '.*>InnerMap'") {
                val result = validateConfig(
                    yamlConfig("config_validation/other-nested-property-names.yml"),
                    baseline,
                    patterns(".*>InnerMap")
                )

                assertThat(result).contains(
                    propertyDoesNotExists("complexity>LargeClass>howMany"),
                    propertyDoesNotExists("complexity>LongLongMethod"),
                    propertyDoesNotExists("complexity>LongParameterList>enabled")
                )

                assertThat(result).doesNotContain(
                    propertyDoesNotExists("complexity>InnerMap>InnerKey"),
                    propertyDoesNotExists("complexity>InnerMap>Inner2>nestedActive")
                )
            }
        }
    }
})
