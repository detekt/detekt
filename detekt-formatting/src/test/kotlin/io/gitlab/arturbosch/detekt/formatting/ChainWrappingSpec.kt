package io.gitlab.arturbosch.detekt.formatting

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainWrapping
import dev.detekt.test.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChainWrappingSpec {

    @Test
    fun `should work like KtLint`() {
        val subject = loadFile("configTests/chain-wrapping-before.kt")
        val expected = loadFileContent("configTests/chain-wrapping-after.kt")

        val findings = ChainWrapping(Config.empty).lint(subject.text)

        assertThat(findings).isNotEmpty()
        assertThat(subject.text).isEqualTo(expected)
    }
}
