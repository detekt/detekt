package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.test.utils.createTempDirectoryForTest
import io.github.detekt.tooling.api.InvalidConfig
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigValidator
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.createProcessingSettings
import io.gitlab.arturbosch.detekt.test.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThatCode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SupportConfigValidationSpec : Spek({

    describe("support config validation") {

        val testDir by memoized { createTempDirectoryForTest("detekt-sample") }

        it("fails when unknown properties are found") {
            val config = yamlConfigFromContent("""
                # Properties of custom rule sets get excluded by default.
                sample-rule-set:
                  TooManyFunctions:
                    active: true

                # This properties are unknown to detekt and must be excluded.
                my_additional_properties:
                  magic_number: 7
                  magic_string: 'Hello World'
            """)
            createProcessingSettings(testDir, config).use {
                assertThatCode { checkConfiguration(it) }
                    .isInstanceOf(InvalidConfig::class.java)
                    .hasMessageContaining("Run failed with 1 invalid config property.")
                    .hasMessageContaining("my_additional_properties")
            }
        }

        it("fails due to custom config validator want active to be booleans") {
            val config = yamlConfigFromContent("""
                # Properties of custom rule sets get excluded by default.
                sample-rule-set:
                  TooManyFunctions:
                    # This property is tested via the SampleConfigValidator
                    active: 1 # should be true
            """)
            createProcessingSettings(testDir, config).use {
                assertThatCode { checkConfiguration(it) }
                    .isInstanceOf(InvalidConfig::class.java)
                    .hasMessageContaining("Run failed with 1 invalid config property.")
            }
        }

        it("passes with excluded new properties") {
            val config = yamlConfigFromContent("""
               config:
                 validation: true
                 # Additional properties can be useful when writing custom extensions.
                 # However only properties defined in the default config are known to detekt.
                 # All unknown properties are treated as errors if not excluded.
                 excludes: 'my_additional_properties'

               # Properties of custom rule sets get excluded by default.
               # If you want to validate them further, consider implementing a ConfigValidator.
               sample-rule-set:
                 TooManyFunctions:
                   active: true

               # This properties are unknown to detekt and must be excluded.
               my_additional_properties:
                 magic_number: 7
                 magic_string: 'Hello World'
            """)
            createProcessingSettings(testDir, config).use {
                assertThatCode { checkConfiguration(it) }.doesNotThrowAnyException()
            }
        }
    }
})

internal class SampleRuleProvider : RuleSetProvider {

    override val ruleSetId: String = "sample-rule-set"

    override fun instance(config: Config) = RuleSet(ruleSetId, emptyList())
}

internal class SampleConfigValidator : ConfigValidator {

    override fun validate(config: Config): Collection<Notification> {
        val result = mutableListOf<Notification>()
        runCatching {
            config.subConfig("sample-rule-set")
                .subConfig("TooManyFunctions")
                .valueOrNull<Boolean>("active")
        }.onFailure {
            result.add(SimpleNotification("'active' property must be of type boolean."))
        }
        return result
    }
}
