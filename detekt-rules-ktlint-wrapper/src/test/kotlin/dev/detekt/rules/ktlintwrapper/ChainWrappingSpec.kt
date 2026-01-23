package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.ChainWrapping
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChainWrappingSpec {

    @Test
    fun `should work like KtLint`() {
        val subject = loadFile("configTests/chain-wrapping-before.kt")
        val expected = loadFileContent("configTests/chain-wrapping-after.kt")

        val findings = ChainWrapping(Config.Empty).lint(subject.text)

        assertThat(findings).isNotEmpty()
        assertThat(subject.text).isEqualTo(expected)
    }
}
