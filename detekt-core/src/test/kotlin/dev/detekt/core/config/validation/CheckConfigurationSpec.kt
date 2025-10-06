package dev.detekt.core.config.validation

import dev.detekt.api.Config
import dev.detekt.api.ConfigValidator
import dev.detekt.api.Notification
import dev.detekt.api.Notification.Level
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider
import dev.detekt.core.createNullLoggingSpec
import dev.detekt.core.createProcessingSettings
import dev.detekt.core.tooling.getDefaultConfiguration
import dev.detekt.test.yamlConfigFromContent
import dev.detekt.tooling.api.InvalidConfig
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class CheckConfigurationSpec(@TempDir private val testDir: Path) {

    private val spec = createNullLoggingSpec {}

    @Test
    fun `passes because config validation is disabled by tooling spec`() {
        val config = yamlConfigFromContent(
            """
                unknown_property:
                  unknown_var: ""
            """.trimIndent()
        )
        createProcessingSettings(
            testDir,
            config,
        ) {
            config {
                shouldValidateBeforeAnalysis = false
            }
        }.use {
            assertThatCode { checkConfiguration(it, spec.getDefaultConfiguration()) }
                .doesNotThrowAnyException()
        }
    }

    @Test
    fun `fails when unknown properties are found`() {
        val config = yamlConfigFromContent(
            """
                # Properties of custom rule sets get excluded by default.
                sample-rule-set:
                  TooManyFunctions:
                    active: true
                
                # This properties are unknown to detekt and must be excluded.
                my_additional_properties:
                  magic_number: 7
                  magic_string: 'Hello World'
            """.trimIndent()
        )
        createProcessingSettings(testDir, config).use {
            assertThatCode { checkConfiguration(it, spec.getDefaultConfiguration()) }
                .isInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("Run failed with 1 invalid config property.")
                .hasMessageContaining("my_additional_properties")
        }
    }

    @Test
    fun `fails due to custom config validator want active to be booleans`() {
        val config = yamlConfigFromContent(
            """
                # Properties of custom rule sets get excluded by default.
                sample-rule-set:
                  TooManyFunctions:
                    # This property is tested via the SampleConfigValidator
                    active: 1 # should be true
            """.trimIndent()
        )
        createProcessingSettings(testDir, config).use {
            assertThatCode { checkConfiguration(it, spec.getDefaultConfiguration()) }
                .isInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("Run failed with 1 invalid config property.")
        }
    }

    @Test
    fun `passes with excluded new properties`() {
        val config = yamlConfigFromContent(
            """
                config:
                  validation: true
                  # Additional properties can be useful when writing custom extensions.
                  # However only properties defined in the default config are known to detekt.
                  # All unknown properties are treated as errors if not excluded.
                  excludes: ['my_additional_properties']
                
                # Properties of custom rule sets get excluded by default.
                # If you want to validate them further, consider implementing a ConfigValidator.
                sample-rule-set:
                  TooManyFunctions:
                    active: true
                
                # This properties are unknown to detekt and must be excluded.
                my_additional_properties:
                  magic_number: 7
                  magic_string: 'Hello World'
            """.trimIndent()
        )
        createProcessingSettings(testDir, config).use {
            assertThatCode { checkConfiguration(it, spec.getDefaultConfiguration()) }
                .doesNotThrowAnyException()
        }
    }
}

class SampleRuleProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("sample-rule-set")

    override fun instance() = RuleSet(ruleSetId, emptyList())
}

class SampleConfigValidator : ConfigValidator {

    override val id: String = "SampleConfigValidator"

    override fun validate(config: Config): Collection<Notification> {
        val result = mutableListOf<Notification>()
        runCatching {
            config.subConfig("sample-rule-set")
                .subConfig("TooManyFunctions")
                .valueOrNull<Boolean>("active")
        }.onFailure {
            result.add(Notification("'active' property must be of type boolean.", Level.Error))
        }
        return result
    }
}
