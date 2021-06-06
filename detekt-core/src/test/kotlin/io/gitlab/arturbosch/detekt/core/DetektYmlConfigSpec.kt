package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.core.config.DefaultConfig
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DetektYmlConfigSpec : Spek({

    describe("detekt YAML config") {

        val config by memoized { DefaultConfig.newInstance() }

        ruleSetsNamesToPackage.forEach { (name, packageName) ->
            it("$name section") {
                ConfigAssert(config, name, packageName).assert()
            }
        }

        it("is backed by a yaml file") {
            assertThat(config).isInstanceOf(YamlConfig::class.java)
        }

        it("contains all general config keys") {
            val yamlConfig = config as YamlConfig

            val topLevelConfigKeys = yamlConfig.properties.keys

            assertThat(topLevelConfigKeys).containsAll(generalConfigKeys)
        }

        it("is completely checked") {
            val yamlConfig = config as YamlConfig
            val checkedRuleSetNames = ruleSetsNamesToPackage.map { it.first }

            val topLevelConfigKeys = yamlConfig.properties.keys

            assertThat(topLevelConfigKeys - generalConfigKeys)
                .containsExactlyInAnyOrderElementsOf(checkedRuleSetNames)
        }
    }
})

private val ruleSetsNamesToPackage: List<Pair<String, String>> = listOf(
    "complexity" to "io.gitlab.arturbosch.detekt.rules.complexity",
    "coroutines" to "io.gitlab.arturbosch.detekt.rules.coroutines",
    "comments" to "io.gitlab.arturbosch.detekt.rules.documentation",
    "empty-blocks" to "io.gitlab.arturbosch.detekt.rules.empty",
    "exceptions" to "io.gitlab.arturbosch.detekt.rules.exceptions",
    "formatting" to "io.gitlab.arturbosch.detekt.formatting",
    "naming" to "io.gitlab.arturbosch.detekt.rules.naming",
    "performance" to "io.gitlab.arturbosch.detekt.rules.performance",
    "potential-bugs" to "io.gitlab.arturbosch.detekt.rules.bugs",
    "style" to "io.gitlab.arturbosch.detekt.rules.style",
)

private val generalConfigKeys = listOf(
    "build",
    "config",
    "processors",
    "console-reports",
    "output-reports"
)
