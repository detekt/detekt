package dev.detekt.tooling.api.spec

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path

class BaselineSpecSpec {

    @Test
    fun `defaults fragment directory to null for existing implementations`() {
        val baselineSpec = object : BaselineSpec {
            override val path: Path? = null
            override val shouldCreateDuringAnalysis: Boolean = false
        }

        assertThat(baselineSpec.fragmentDirectory).isNull()
    }
}
