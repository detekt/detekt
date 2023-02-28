@file:Suppress("DEPRECATION")

package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.resourceAsPath
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

        @Suppress("UnusedPrivateMember")
        private fun runnerConfigs(): List<Arguments> {
            return listOf(
                arguments(arrayOf("--generate-config"), ConfigExporter::class),
                arguments(arrayOf("--run-rule", "RuleSet:Rule"), Runner::class),
                arguments(arrayOf("--version"), VersionPrinter::class),
                arguments(emptyArray<String>(), Runner::class),
            )
        }

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
}
