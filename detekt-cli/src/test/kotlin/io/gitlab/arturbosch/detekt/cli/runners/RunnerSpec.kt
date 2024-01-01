package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.IssuesFound
import io.gitlab.arturbosch.detekt.cli.executeDetekt
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.readLines

class RunnerSpec {

    val inputPath = resourceAsPath("cases/Poko.kt")

    @Nested
    inner class `executes the runner with create baseline` {

        @Test
        fun `should not throw`() {
            val tmpReport = createTempFileForTest("RunnerSpec", ".txt")

            executeDetekt(
                "--input",
                inputPath.toString(),
                "--baseline",
                resourceAsPath("configs/baseline-empty.xml").toString(),
                "--create-baseline",
                "--report",
                "txt:$tmpReport",
            )

            assertThat(tmpReport).hasContent("")
        }
    }

    @Nested
    inner class `customize output and error printers` {

        private val outPrintStream = StringPrintStream()
        private val errPrintStream = StringPrintStream()

        @Nested
        inner class `execute with default config without issues` {

            val path: Path = resourceAsPath("/cases/CleanPoko.kt")

            @BeforeEach
            fun setUp() {
                val args = parseArguments(arrayOf("--input", path.toString()))

                Runner(args, outPrintStream, errPrintStream).execute()
            }

            @Test
            fun `writes no build related output to output printer`() {
                assertThat(outPrintStream.toString()).doesNotContain("A failure - [test]")
            }

            @Test
            fun `does not write anything to error printer`() {
                assertThat(errPrintStream.toString()).isEmpty()
            }
        }

        @Nested
        inner class `execute with issues` {

            @BeforeEach
            fun setUp() {
                val args = parseArguments(
                    arrayOf(
                        "--input",
                        inputPath.toString(),
                        "--config-resource",
                        "/configs/valid-config.yml"
                    )
                )

                assertThatThrownBy { Runner(args, outPrintStream, errPrintStream).execute() }
                    .isExactlyInstanceOf(IssuesFound::class.java)
            }

            @Test
            fun `writes output to output printer`() {
                assertThat(outPrintStream.toString()).contains("A failure [test]")
            }

            @Test
            fun `does not write anything to error printer`() {
                assertThat(errPrintStream.toString()).isEmpty()
            }
        }
    }

    @Nested
    inner class `with config validation` {

        val path: Path = resourceAsPath("/cases/CleanPoko.kt")

        @Test
        fun `should throw on invalid config property when validation=true`() {
            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--config-resource",
                    "/configs/invalid-config.yml"
                )
            }.isExactlyInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("property")
        }

        @Test
        fun `should throw on invalid config properties when validation=true`() {
            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--config-resource",
                    "/configs/invalid-configs.yml"
                )
            }.isExactlyInstanceOf(InvalidConfig::class.java)
                .hasMessageContaining("properties")
        }

        @Test
        fun `should not throw on invalid config property when validation=false`() {
            assertThatCode {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--config-resource",
                    "/configs/invalid-config_no-validation.yml"
                )
            }.doesNotThrowAnyException()
        }

        @Test
        fun `should not throw on deprecation warnings`() {
            assertThatCode {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "--config-resource",
                    "/configs/deprecated-property.yml"
                )
            }.doesNotThrowAnyException()
        }
    }

    @Nested
    inner class `executes the runner for a single rule` {

        @Test
        fun `should load and run custom rule`() {
            val tmp = createTempFileForTest("SingleRuleRunnerSpec", ".txt")

            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    inputPath.toString(),
                    "--report",
                    "txt:$tmp",
                    "--run-rule",
                    "test:test",
                    "--config-resource",
                    "/configs/valid-config.yml"
                )
            }.isExactlyInstanceOf(IssuesFound::class.java)
            assertThat(tmp.readLines()).hasSize(1)
        }

        @Test
        fun `should throw on non existing rule`() {
            assertThatThrownBy { executeDetekt("--run-rule", "test:non_existing") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `should throw on non existing rule set`() {
            assertThatThrownBy { executeDetekt("--run-rule", "non_existing:test") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `should throw on non existing run-rule`() {
            assertThatThrownBy { executeDetekt("--run-rule", "") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Pattern 'RuleSetId:RuleId' expected.")
        }
    }

    @Test
    fun `does not fail on rule property type change from comma separated string to list when YamlConfig is wrapped`() {
        assertThatCode {
            executeDetekt(
                "--all-rules", // wrapping config
                "--input",
                inputPath.toString(),
                "--config-resource",
                "configs/return-count-with-string-property.yml"
            )
        }.isExactlyInstanceOf(IssuesFound::class.java)
    }
}
