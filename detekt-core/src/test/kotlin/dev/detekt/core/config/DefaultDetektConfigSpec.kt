package dev.detekt.core.config

import dev.detekt.utils.getSafeResourceAsStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class DefaultDetektConfigSpec {
    private val generalConfigKeys = listOf("config", "processors")

    private val config: YamlConfig = YamlConfig.load(
        DefaultDetektConfigSpec::class.java.getSafeResourceAsStream("/default-detekt-config.yml")!!.reader()
    )

    private fun ruleSetsNamesToPackage(): List<Arguments> =
        listOf(
            arguments("complexity", "dev.detekt.rules.complexity"),
            arguments("coroutines", "dev.detekt.rules.coroutines"),
            arguments("comments", "dev.detekt.rules.comments"),
            arguments("empty-blocks", "dev.detekt.rules.emptyblocks"),
            arguments("exceptions", "dev.detekt.rules.exceptions"),
            arguments("naming", "dev.detekt.rules.naming"),
            arguments("performance", "dev.detekt.rules.performance"),
            arguments("potential-bugs", "dev.detekt.rules.potentialbugs"),
            arguments("style", "dev.detekt.rules.style"),
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
