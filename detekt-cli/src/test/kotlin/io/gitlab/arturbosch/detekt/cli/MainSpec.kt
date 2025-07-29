@file:Suppress("DEPRECATION")

package io.gitlab.arturbosch.detekt.cli

import dev.detekt.test.utils.NullPrintStream
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.InvalidConfig
import dev.detekt.tooling.api.IssuesFound
import dev.detekt.tooling.api.UnexpectedError
import dev.detekt.tooling.internal.DefaultAnalysisResult
import dev.detekt.tooling.internal.EmptyContainer
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.reflect.KClass

class MainSpec {

    @Nested
    inner class `Build runner` {

        @Suppress("UnusedPrivateFunction")
        private fun runnerConfigs(): List<Arguments> =
            listOf(
                arguments(arrayOf("--generate-config", "detekt-test.yml"), ConfigExporter::class),
                arguments(arrayOf("--run-rule", "RuleSet:Rule"), Runner::class),
                arguments(arrayOf("--version"), VersionPrinter::class),
                arguments(emptyArray<String>(), Runner::class),
            )

        @ParameterizedTest
        @MethodSource("runnerConfigs")
        fun `builds correct runner`(args: Array<String>, expectedRunnerClass: KClass<*>) {
            val runner = buildRunner(args, NullPrintStream(), NullPrintStream())

            assertThat(runner).isExactlyInstanceOf(expectedRunnerClass.java)
        }
    }

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

            buildRunner(args, out, err)

            assertThat(err.toString()).isEmpty()
        }

        @Test
        fun `succeeds with --baseline if the path exists and is a file`() {
            val out = StringPrintStream()
            val err = StringPrintStream()

            val path = resourceAsPath("/configs/baseline-empty.xml")

            val args = arrayOf("--baseline", path.toString())

            buildRunner(args, out, err)

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
