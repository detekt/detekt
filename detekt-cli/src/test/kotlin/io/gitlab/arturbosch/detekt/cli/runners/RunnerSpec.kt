package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.StringPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.IssuesFound
import io.gitlab.arturbosch.detekt.cli.executeDetekt
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class RunnerSpec {

    val inputPath = resourceAsPath("cases/Poko.kt")

    @Nested
    inner class `executes the runner with create baseline` {

        @Test
        fun `should not throw`() {
            executeDetekt(
                "--input",
                inputPath.toString(),
                "--baseline",
                resourceAsPath("configs/baseline-empty.xml").toString(),
                "--create-baseline",
            )
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
                assertThat(outPrintStream.toString()).contains("A failure [TestRule]")
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
            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    inputPath.toString(),
                    "--run-rule",
                    "test:TestRule",
                    "--config-resource",
                    "/configs/valid-config.yml"
                )
            }
                .isExactlyInstanceOf(IssuesFound::class.java)
                .hasMessage("Analysis failed with 1 issues.")
        }

        @Test
        fun `should throw on non existing rule`() {
            assertThatThrownBy { executeDetekt("--run-rule", "test:non_existing") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `should throw on non existing rule set`() {
            assertThatThrownBy { executeDetekt("--run-rule", "non_existing:TestRule") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `should throw on non existing run-rule`() {
            assertThatThrownBy { executeDetekt("--run-rule", "") }
                .isExactlyInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Pattern 'RuleSetId:RuleName' expected.")
        }
    }

    @Nested
    inner class AutoCorrect {
        private val outPrintStream = StringPrintStream()
        private val errPrintStream = StringPrintStream()

        private val config = resourceAsPath("/configs/formatting-config.yml")

        private val args = arrayOf(
            "--auto-correct",
            "--config",
            config.toString(),
            "--input",
        )

        private val modificationMessagePrefix = "File "
        private val modificationMessageSuffix = " was modified"

        @Test
        fun `succeeds with --autocorrect with zero autocorrectable fixes`() {
            val inputPath = resourceAsPath("/autocorrect/CompliantSample.kt")

            assertThatCode {
                Runner(parseArguments(args + inputPath.toString()), outPrintStream, errPrintStream).execute()
            }.doesNotThrowAnyException()

            assertThat(errPrintStream.toString()).isEmpty()
            assertThat(outPrintStream.toString())
                .doesNotContain("$modificationMessagePrefix${inputPath.absolutePathString()}$modificationMessageSuffix")
        }

        @Test
        fun `succeeds with --autocorrect with single autocorrectable fix`() {
            val inputPath = resourceAsPath("/autocorrect/SingleRule.kt")

            Runner(parseArguments(args + inputPath.toString()), outPrintStream, errPrintStream).execute()

            assertThat(errPrintStream.toString()).isEmpty()
            assertThat(inputPath).content().isEqualToNormalizingNewlines(
                """
                    class Test {
    
                    }
    
                """.trimIndent()
            )
        }

        @Test
        fun `succeeds with --autocorrect with multiple autocorrectable fixes`() {
            val inputPath = resourceAsPath("/autocorrect/MultipleRules.kt")

            Runner(parseArguments(args + inputPath.toString()), outPrintStream, errPrintStream).execute()

            assertThat(errPrintStream.toString()).isEmpty()
            assertThat(inputPath).content().isEqualToNormalizingNewlines(
                """
                    class Test {
    
                        val foo =
                            listOf(1, 2, 3)
                            .filter { it > 2 }!!
                            .takeIf { it.count() > 100 }
                            ?.sum()
                        val foobar =
                            foo()
                                ?: bar
    
                    }
    
                """.trimIndent()
            )
        }

        @Test
        fun `keeps LF line endings after autocorrect`() {
            val inputPath = resourceAsPath("/autocorrect/SingleRuleLF.kt")

            Runner(parseArguments(args + inputPath.toString()), outPrintStream, errPrintStream).execute()

            assertThat(errPrintStream.toString()).isEmpty()
            assertThat(inputPath).content().isEqualTo("class Test {\n\n}\n")
        }

        @Test
        fun `keeps CRLF line endings after autocorrect`() {
            val inputPath = resourceAsPath("/autocorrect/SingleRuleCRLF.kt")

            Runner(parseArguments(args + inputPath.toString()), outPrintStream, errPrintStream).execute()

            assertThat(errPrintStream.toString()).isEmpty()
            assertThat(inputPath).content().isEqualTo("class Test {\r\n\r\n}\r\n")
        }
    }

    @Nested
    inner class CompilerArgs {
        @Test
        fun `accepts valid compiler options that are not natively handed by detekt CLI`() {
            val path = resourceAsPath("/cases/CleanPoko.kt")
            assertThatCode {
                executeDetekt(
                    "--input",
                    path.toString(),
                    "-Xcontext-receivers",
                    "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi",
                )
            }.doesNotThrowAnyException()
        }

        @Test
        fun `throws HandledArgumentViolation on wrong options`() {
            assertThatIllegalStateException()
                .isThrownBy { executeDetekt("--unknown-to-us-all") }
        }
    }
}
