package dev.detekt.rules.ktlintwrapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DefaultConfigSpec {

    private val defaultConfig =
        checkNotNull(KtlintWrapperProvider::class.java.getResource("/config/config.yml")) {
            "The bundled default config is missing"
        }.readText()

    /**
     * Declaring `autoCorrect` on every single rule makes it impossible to turn correction off for a
     * whole rule set: once the user config is merged with the default one, the rule level values win
     * and the user's `ktlint: autoCorrect: false` is unreachable. See #1466.
     *
     * Which rules are able to correct is decided at runtime by [dev.detekt.api.AutoCorrectable]
     * instead, so the default config only carries the rule set level switch.
     */
    @Test
    fun `declares autoCorrect only at the rule set level`() {
        val declarations = defaultConfig.lines().filter { "autoCorrect:" in it }

        assertThat(declarations).containsExactly("  autoCorrect: true")
    }
}
