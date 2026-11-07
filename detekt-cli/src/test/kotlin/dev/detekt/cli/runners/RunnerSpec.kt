package dev.detekt.cli.runners

import dev.detekt.cli.parseArguments
import dev.detekt.test.utils.NullPrintStream
import dev.detekt.test.utils.StringPrintStream
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.InvalidConfig
import dev.detekt.tooling.api.IssuesFound
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

class RunnerSpec {

    val inputPath = resourceAsPath("cases/Poko.kt")

    private fun createConfigJar(jarPath: Path, resourcePath: String, config: String) {
        JarOutputStream(Files.newOutputStream(jarPath)).use { jar ->
            jar.putNextEntry(JarEntry(resourcePath))
            jar.write(config.toByteArray())
            jar.closeEntry()
        }
    }

    @Test
    fun `Can pass resource path within JAR to --config-resource`(@TempDir tempDir: Path) {
        val content = """
            nonexistent-ruleset:
              active: false
        """.trimIndent()

        val tempJar = tempDir.resolve("temp.jar")
        val detektJar = System.getProperty("detektJarPath")

        createConfigJar(tempJar, "resources/config/path/failingconfig.yml", content)

        val java = Path(
            System.getProperty("java.home"),
            "bin",
            "java",
        )

        val process = ProcessBuilder(
            java.toString(),
            "-cp",
            listOf(detektJar, tempJar.toString()).joinToString(File.pathSeparator),
            "dev.detekt.cli.Main",
            "--input",
            inputPath.toString(),
            "--config-resource",
            "resources/config/path/failingconfig.yml",
        )
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream
            .bufferedReader()
            .readText()

        process.waitFor()

        assertThat(output).contains("Property 'nonexistent-ruleset' is misspelled or does not exist.")
    }

    @Test
    fun `executes the runner with create baseline`(@TempDir tempDir: Path) {
        val baseline = tempDir.resolve("baseline.xml")
        executeDetekt(
            "--input",
            inputPath.toString(),
            "--config-resource",
            "/configs/valid-config.yml",
            "--baseline",
            baseline.toString(),
            "--create-baseline",
        )

        assertThat(baseline).content().contains("<SmellBaseline>")
    }

    @Nested
    inner class `customize output and error printers` {
        @Test
        fun `Default configuration without issues`() {
            val path = resourceAsPath("cases/CleanPoko.kt")
            val outPrintStream = StringPrintStream()
            val errPrintStream = StringPrintStream()

            executeDetekt("--input", path.toString(), out = outPrintStream, err = errPrintStream)

            assertThat(outPrintStream.toString()).isEmpty()
            assertThat(errPrintStream.toString()).isEmpty()
        }

        @Test
        fun `Default configuration with issues`() {
            val path = resourceAsPath("cases/Poko.kt")
            val outPrintStream = StringPrintStream()
            val errPrintStream = StringPrintStream()

            assertThatThrownBy {
                executeDetekt("--input", path.toString(), out = outPrintStream, err = errPrintStream)
            }
                .isExactlyInstanceOf(IssuesFound::class.java)

            assertThat(outPrintStream.toString()).contains(path.toString())
            assertThat(errPrintStream.toString()).isEmpty()
        }

        @Test
        fun `With issues the output is only on outPrint`() {
            val outPrintStream = StringPrintStream()
            val errPrintStream = StringPrintStream()

            assertThatThrownBy {
                executeDetekt(
                    "--input",
                    inputPath.toString(),
                    "--config-resource",
                    "/configs/valid-config.yml",
                    out = outPrintStream,
                    err = errPrintStream,
                )
            }
                .isExactlyInstanceOf(IssuesFound::class.java)

            assertThat(outPrintStream.toString()).contains("A failure [TestRule]")
            assertThat(errPrintStream.toString()).isEmpty()
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

        private val config = resourceAsPath("/configs/ktlint-config.yml")

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
                executeDetekt(*args.plus(inputPath.toString()), out = outPrintStream, err = errPrintStream)
            }.doesNotThrowAnyException()

            assertThat(errPrintStream.toString()).isEmpty()
            assertThat(outPrintStream.toString())
                .doesNotContain("$modificationMessagePrefix${inputPath.absolutePathString()}$modificationMessageSuffix")
        }

        @Test
        fun `succeeds with --autocorrect with single autocorrectable fix`() {
            val inputPath = resourceAsPath("/autocorrect/SingleRule.kt")

            executeDetekt(*args.plus(inputPath.toString()), out = outPrintStream, err = errPrintStream)

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

            executeDetekt(*args.plus(inputPath.toString()), out = outPrintStream, err = errPrintStream)

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

            executeDetekt(*args.plus(inputPath.toString()), out = outPrintStream, err = errPrintStream)

            assertThat(errPrintStream.toString()).isEmpty()
            @Suppress("StringShouldBeRawString")
            assertThat(inputPath).content().isEqualTo("class Test {\n\n}\n")
        }

        @Test
        fun `keeps CRLF line endings after autocorrect`() {
            val inputPath = resourceAsPath("/autocorrect/SingleRuleCRLF.kt")

            executeDetekt(*args.plus(inputPath.toString()), out = outPrintStream, err = errPrintStream)

            assertThat(errPrintStream.toString()).isEmpty()
            @Suppress("StringShouldBeRawString")
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

private fun executeDetekt(
    vararg args: String,
    out: PrintStream = NullPrintStream(),
    err: PrintStream = NullPrintStream(),
) {
    Runner(parseArguments(args), out, err).execute()
}
