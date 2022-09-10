package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.ParameterException
import io.github.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

internal class CliArgsSpec {

    private val projectPath: Path = resourceAsPath("/").parent.parent.parent.parent.toAbsolutePath()

    @Nested
    inner class `Parsing the input path` {

        @Test
        fun `the current working directory is used if parameter is not set`() {
            val cli = parseArguments(emptyArray())
            assertThat(cli.inputPaths).hasSize(1)
            assertThat(cli.inputPaths.first()).isEqualTo(Paths.get(System.getProperty("user.dir")))
        }

        @Test
        fun `a single value is converted to a path`() {
            val cli = parseArguments(arrayOf("--input", "$projectPath"))
            assertThat(cli.inputPaths).hasSize(1)
            assertThat(cli.inputPaths.first().toAbsolutePath()).isEqualTo(projectPath)
        }

        @Test
        fun `multiple input paths can be separated by comma`() {
            val mainPath = projectPath.resolve("src/main").toAbsolutePath()
            val testPath = projectPath.resolve("src/test").toAbsolutePath()
            val cli = parseArguments(arrayOf("--input", "$mainPath,$testPath"))
            assertThat(cli.inputPaths).hasSize(2)
            assertThat(cli.inputPaths.map(Path::toAbsolutePath)).containsExactlyInAnyOrder(mainPath, testPath)
        }

        @Test
        fun `reports an error if the input path does not exist`() {
            val pathToNonExistentDirectory = projectPath.resolve("nonExistent")
            val params = arrayOf("--input", "$pathToNonExistentDirectory")

            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { parseArguments(params).inputPaths }
                .withMessageContaining("does not exist")
        }
    }

    @Nested
    inner class `parsing config parameters` {

        @Test
        fun `should fail on invalid config value`() {
            assertThatIllegalArgumentException()
                .isThrownBy { parseArguments(arrayOf("--config", ",")).toSpec() }
            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { parseArguments(arrayOf("--config", "sfsjfsdkfsd")).toSpec() }
            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { parseArguments(arrayOf("--config", "./i.do.not.exist.yml")).toSpec() }
        }
    }

    @Nested
    inner class `Valid combination of options` {

        @Nested
        inner class `Baseline feature` {

            @Test
            fun `reports an error when using --create-baseline without a --baseline file`() {
                assertThatCode { parseArguments(arrayOf("--create-baseline")) }
                    .isInstanceOf(HandledArgumentViolation::class.java)
                    .hasMessageContaining("Creating a baseline.xml requires the --baseline parameter to specify a path")
            }

            @Test
            fun `reports an error when using --baseline file does not exist`() {
                val nonExistingDirectory = projectPath.resolve("nonExistent").toString()
                assertThatCode { parseArguments(arrayOf("--baseline", nonExistingDirectory)) }
                    .isInstanceOf(HandledArgumentViolation::class.java)
                    .hasMessageContaining("The file specified by --baseline should exist '$nonExistingDirectory'.")
            }

            @Test
            fun `reports an error when using --baseline file which is not a file`() {
                val directory = resourceAsPath("/cases").toString()
                assertThatCode { parseArguments(arrayOf("--baseline", directory)) }
                    .isInstanceOf(HandledArgumentViolation::class.java)
                    .hasMessageContaining("The path specified by --baseline should be a file '$directory'.")
            }
        }

        @Test
        fun `throws HelpRequest on --help`() {
            assertThatExceptionOfType(HelpRequest::class.java)
                .isThrownBy { parseArguments(arrayOf("--help")) }
        }

        @Test
        fun `throws HandledArgumentViolation on wrong options`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--unknown-to-us-all")) }
        }
    }

    @Test
    fun `--all-rules lead to all rules being activated`() {
        val spec = parseArguments(arrayOf("--all-rules")).toSpec()
        assertThat(spec.rulesSpec.activateAllRules).isTrue()
    }

    @Nested
    inner class `type resolution parameters are accepted` {

        @Test
        fun `--jvm-target is accepted`() {
            val spec = parseArguments(arrayOf("--jvm-target", "11")).toSpec()
            assertThat(spec.compilerSpec.jvmTarget).isEqualTo("11")
        }

        @Test
        fun `--jvm-target with decimal is accepted`() {
            val spec = parseArguments(arrayOf("--jvm-target", "1.8")).toSpec()
            assertThat(spec.compilerSpec.jvmTarget).isEqualTo("1.8")
        }

        @Test
        fun `--language-version is accepted`() {
            val spec = parseArguments(arrayOf("--language-version", "1.6")).toSpec()
            assertThat(spec.compilerSpec.languageVersion).isEqualTo("1.6")
        }
    }
}
