package io.gitlab.arturbosch.detekt.generator.config

import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths
import java.util.stream.Stream

class DetektYmlConfigSpec {

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

    private val config: YamlConfig = YamlConfig.load(
        Paths.get("../detekt-core/src/main/resources/default-detekt-config.yml").toAbsolutePath()
    ) as YamlConfig

    fun ruleSetsNamesToPackageArguments(): Stream<Arguments> =
        ruleSetsNamesToPackage.stream().map { arguments(it.first, it.second) }

    @ParameterizedTest
    @MethodSource("ruleSetsNamesToPackageArguments")
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
        val checkedRuleSetNames = ruleSetsNamesToPackage.map { it.first }

        val topLevelConfigKeys = config.properties.keys

        assertThat(topLevelConfigKeys - generalConfigKeys)
            .containsExactlyInAnyOrderElementsOf(checkedRuleSetNames)
    }
}
