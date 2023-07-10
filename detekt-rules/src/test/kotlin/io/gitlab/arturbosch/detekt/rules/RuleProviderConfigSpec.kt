package io.gitlab.arturbosch.detekt.rules

import io.github.classgraph.ClassGraph
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RuleProviderConfigSpec {

    @Test
    fun `should test if the config has been passed to all rules`() {
        val config = TestConfig()
        val providers = ClassGraph()
            .acceptPackages("io.gitlab.arturbosch.detekt.rules")
            .scan()
            .use { scanResult ->
                scanResult.getClassesImplementing(DefaultRuleSetProvider::class.java)
                    .loadClasses(DefaultRuleSetProvider::class.java)
            }

        providers.forEach {
            val provider = it.getDeclaredConstructor().newInstance()
            val ruleSet = provider.instance(config)
            ruleSet.rules.forEach { baseRule ->
                val rule = baseRule as? Rule
                if (rule != null) {
                    assertThat(rule.ruleSetConfig)
                        .withFailMessage("No config was passed to ${rule.javaClass.name}")
                        .isEqualTo(config)
                }
            }
        }
    }
}
