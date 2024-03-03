package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.ParameterException
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.spec.RulesSpec.FailurePolicy.FailOnSeverity
import io.github.detekt.tooling.api.spec.RulesSpec.FailurePolicy.NeverFail
import io.gitlab.arturbosch.detekt.api.Severity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute

internal class CliArgsSpec {

    private val projectPath: Path = resourceAsPath("/").parent.parent.parent.parent.absolute()

    @Nested
    inner class `Parsing the input path` {

        @Test
        fun `the current working directory is used if parameter is not set`() {
            val cli = parseArguments(emptyArray())
            assertThat(cli.inputPaths).hasSize(1)
            assertThat(cli.inputPaths.first()).isEqualTo(Path(System.getProperty("user.dir")))
        }

        @Test
        fun `a single value is converted to a path`() {
            val cli = parseArguments(arrayOf("--input", "$projectPath"))
            assertThat(cli.inputPaths).hasSize(1)
            assertThat(cli.inputPaths.first().absolute()).isEqualTo(projectPath)
        }

        @Test
        fun `multiple input paths can be separated by comma`() {
            val mainPath = projectPath.resolve("src/main").absolute()
            val testPath = projectPath.resolve("src/test").absolute()
            val cli = parseArguments(arrayOf("--input", "$mainPath,$testPath"))
            assertThat(cli.inputPaths).hasSize(2)
            assertThat(cli.inputPaths.map(Path::absolute)).containsExactlyInAnyOrder(mainPath, testPath)
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

    @ParameterizedTest
    @ValueSource(
        strings = [
            "**/one/**,**/two/**,**/three",
            "**/one/**;**/two/**;**/three",
            "**/one/** ,**/two/**, **/three",
            "**/one/** ;**/two/**; **/three",
            "**/one/**,**/two/**;**/three",
            "**/one/** ,**/two/**; **/three",
            " ,,**/one/**,**/two/**,**/three",
        ]
    )
    fun parseExcludes(param: String) {
        val spec = parseArguments(arrayOf("--excludes", param)).toSpec()
        assertThat(spec.projectSpec.excludes).containsExactly("**/one/**", "**/two/**", "**/three")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "**/one/**,**/two/**,**/three",
            "**/one/**;**/two/**;**/three",
            "**/one/** ,**/two/**, **/three",
            "**/one/** ;**/two/**; **/three",
            "**/one/**,**/two/**;**/three",
            "**/one/** ,**/two/**; **/three",
            " ,,**/one/**,**/two/**,**/three",
        ]
    )
    fun parseIncludes(param: String) {
        val spec = parseArguments(arrayOf("--includes", param)).toSpec()
        assertThat(spec.projectSpec.includes).containsExactly("**/one/**", "**/two/**", "**/three")
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

    @Nested
    inner class `Configuration of FailurePolicy` {
        @Test
        fun `not specified results in default value`() {
            val args = emptyArray<String>()

            val actual = parseArguments(args)

            assertThat(actual.failurePolicy).isEqualTo(FailOnSeverity(Severity.Error))
        }

        @Test
        fun `--fail-on-severity never specified results in never fail policy`() {
            val args = arrayOf("--fail-on-severity", "never")

            val actual = parseArguments(args)

            assertThat(actual.failurePolicy).isEqualTo(NeverFail)
        }

        @ParameterizedTest(name = "{0}")
        @EnumSource(value = FailureSeverity::class, names = ["Never"], mode = EnumSource.Mode.EXCLUDE)
        fun `--fail-on-severity`(severity: FailureSeverity) {
            val args = arrayOf("--fail-on-severity", severity.name.lowercase())

            val actual = parseArguments(args)

            assertThat(actual.failurePolicy).isInstanceOf(FailOnSeverity::class.java)
            assertThat((actual.failurePolicy as FailOnSeverity).minSeverity.name)
                .isEqualToIgnoringCase(severity.name)
        }

        @Test
        fun `invalid --fail-on-severity parameter`() {
            val args = arrayOf("--fail-on-severity", "foo")

            assertThatThrownBy {
                parseArguments(args)
            }.isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
