package dev.detekt.core.config.validation

import dev.detekt.core.config.YamlConfig
import dev.detekt.core.yamlConfig
import dev.detekt.core.yamlConfigFromContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MissingRulesConfigValidatorSpec {
    private val baseline = yamlConfig("config_validation/baseline.yml") as YamlConfig
    private val subject = MissingRulesConfigValidator(baseline, DEFAULT_PROPERTY_EXCLUDES.toSet())

    @Test
    fun `do not check for exhaustiveness if disabled by config`() {
        val config = yamlConfigFromContent(
            """
                config:
                  checkExhaustiveness: false
                
                complexity:
                style:
                comments:
            """.trimIndent()
        )

        val result = subject.validate(config)

        assertThat(result).isEmpty()
    }

    @Test
    fun `do not report violations if all rules are mentioned or rule set is disabled`() {
        val config = yamlConfigFromContent(
            """
                config:
                  checkExhaustiveness: true
                
                complexity:
                  active: false
                
                style:
                  WildcardImport:
                    active: true
                  NoElseInWhenExpression:
                    active: true
                  MagicNumber:
                    active: true
                
                comments:
                  CommentOverPrivateProperty:
                    active: false
            """.trimIndent()
        )

        val result = subject.validate(config)

        assertThat(result).isEmpty()
    }

    @Test
    fun `report violations of missing rules and rule sets`() {
        val config = yamlConfigFromContent(
            """
                config:
                  checkExhaustiveness: true
                
                complexity:
                  LongParameterList:
                    active: false
                  LargeClass:
                    active: false
                  InnerMap:
                    Inner1:
                      active: true
                    Inner2:
                      active: true
                
                style:
                  NoElseInWhenExpression:
                    active: true
                  MagicNumber:
                    active: true
                
                comments:
            """.trimIndent()
        )

        val result = subject.validate(config)

        assertThat(result)
            .extracting("message")
            .containsExactlyInAnyOrder(
                "Rule set 'comments' is missing in the configuration.",
                "Rule 'LongMethod' from the 'complexity' rule set is missing in the configuration.",
                "Rule 'WildcardImport' from the 'style' rule set is missing in the configuration.",
            )
    }
}
