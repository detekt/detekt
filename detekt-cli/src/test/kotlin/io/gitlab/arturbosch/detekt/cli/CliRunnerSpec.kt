package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.DetektCli
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CliRunnerSpec {

    @Test
    fun `cli module provides an implementation of DetektCli`() {
        assertThat(DetektCli.load()).isInstanceOf(CliRunner::class.java)
    }
}
