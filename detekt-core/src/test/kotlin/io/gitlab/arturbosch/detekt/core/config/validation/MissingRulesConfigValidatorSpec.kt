package io.gitlab.arturbosch.detekt.core.config.validation

import io.gitlab.arturbosch.detekt.api.internal.CommaSeparatedPattern
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MissingRulesConfigValidatorSpec {
    private val baseline = yamlConfig("config_validation/baseline.yml") as YamlConfig
    private val subject = MissingRulesConfigValidator(
        baseline,
        CommaSeparatedPattern(DEFAULT_PROPERTY_EXCLUDES).mapToRegex()
    )

    @Test
    fun `do not check for exhaustiveness if disabled by config`() {
        val config = yamlConfig("config_validation/exhaustiveness-check-disabled.yml")

        val result = subject.validate(config)

        assertThat(result).isEmpty()
    }

    @Test
    fun `do not report violations if all rules are mentioned or rule set is disabled`() {
        val config = yamlConfig("config_validation/exhaustiveness-check-successful.yml")

        val result = subject.validate(config)

        assertThat(result).isEmpty()
    }

    @Test
    fun `report violations of missing rules and rule sets`() {
        val config = yamlConfig("config_validation/exhaustiveness-check-with-error.yml")

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
