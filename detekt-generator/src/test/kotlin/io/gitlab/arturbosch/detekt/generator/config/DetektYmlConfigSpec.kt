package io.gitlab.arturbosch.detekt.generator.config

import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths

class DetektYmlConfigSpec {

    private val generalConfigKeys = listOf(
        "build",
        "config",
        "processors",
        "console-reports",
        "output-reports"
    )

    private val config: YamlConfig = YamlConfig.load(
        Paths.get("../detekt-core/src/main/resources/default-detekt-config.yml").toAbsolutePath()
    ) as YamlConfig

    private fun ruleSetsNamesToPackage(): List<Arguments> = listOf(
        arguments("complexity", "io.gitlab.arturbosch.detekt.rules.complexity"),
        arguments("coroutines", "io.gitlab.arturbosch.detekt.rules.coroutines"),
        arguments("comments", "io.gitlab.arturbosch.detekt.rules.documentation"),
        arguments("empty-blocks", "io.gitlab.arturbosch.detekt.rules.empty"),
        arguments("exceptions", "io.gitlab.arturbosch.detekt.rules.exceptions"),
        arguments("naming", "io.gitlab.arturbosch.detekt.rules.naming"),
        arguments("performance", "io.gitlab.arturbosch.detekt.rules.performance"),
        arguments("potential-bugs", "io.gitlab.arturbosch.detekt.rules.bugs"),
        arguments("style", "io.gitlab.arturbosch.detekt.rules.style"),
    )

    @ParameterizedTest
    @MethodSource("ruleSetsNamesToPackage")
    fun `section is valid`(name: String, packageName: String) {
        ConfigAssert(config, name, packageName).assert()
    }

    @Test
    fun `contains all general config keys`() {
        val topLevelConfigKeys = config.properties.keys

        assertThat(topLevelConfigKeys).containsAll(generalConfigKeys)
    }

    @Test
    fun `is completely checked`() {
        val checkedRuleSetNames = ruleSetsNamesToPackage().map { it.get()[0] as String }

        val topLevelConfigKeys = config.properties.keys

        assertThat(topLevelConfigKeys - generalConfigKeys)
            .containsExactlyInAnyOrderElementsOf(checkedRuleSetNames)
    }
}
