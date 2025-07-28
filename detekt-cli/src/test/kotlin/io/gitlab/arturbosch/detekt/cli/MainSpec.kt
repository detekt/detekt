@file:Suppress("DEPRECATION")

package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.IssuesFound
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.internal.DefaultAnalysisResult
import io.github.detekt.tooling.internal.EmptyContainer
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MainSpec {

    @Nested
    inner class `Runner creates baselines` {

        @Test
        fun `succeeds with --create-baseline and --baseline`() {
            val out = StringPrintStream()
            val err = StringPrintStream()

            val args = arrayOf(
                "--create-baseline",
                "--baseline",
                "baseline.xml"
            )

            Runner(parseArguments(args).createSpec(out, err))

            assertThat(err.toString()).isEmpty()
        }

        @Test
        fun `succeeds with --baseline if the path exists and is a file`() {
            val out = StringPrintStream()
            val err = StringPrintStream()

            val path = resourceAsPath("/configs/baseline-empty.xml")

            val args = arrayOf("--baseline", path.toString())

            Runner(parseArguments(args).createSpec(out, err))

            assertThat(err.toString()).isEmpty()
        }
    }

    @Nested
    inner class `returns different exit codes based on error` {

        @Test
        fun `returns zero on no error`() {
            assertThat(DefaultAnalysisResult(EmptyContainer, null).exitCode()).isEqualTo(0)
        }

        @Test
        fun `returns one on any UnexpectedError`() {
            val unexpectedError = UnexpectedError(IllegalArgumentException())
            assertThat(DefaultAnalysisResult(null, unexpectedError).exitCode()).isEqualTo(1)
        }

        @Test
        fun `returns two on MaxIssuesReached`() {
            assertThat(DefaultAnalysisResult(null, IssuesFound("")).exitCode()).isEqualTo(2)
        }

        @Test
        fun `returns three on InvalidConfig`() {
            assertThat(DefaultAnalysisResult(null, InvalidConfig("")).exitCode()).isEqualTo(3)
        }
    }
}
