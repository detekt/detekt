package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.DetektCli
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

class CliRunnerSpec : Spek({

    test("cli module provides an implementation of DetektCli") {
        assertThat(DetektCli.load()).isInstanceOf(CliRunner::class.java)
    }
})
