package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.spec.RulesSpec.FailurePolicy.FailOnSeverity
import io.github.detekt.tooling.api.spec.RulesSpec.FailurePolicy.NeverFail
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.io.path.Path
import kotlin.io.path.absolute

internal class CliArgsSpec {

    @Nested
    inner class `Parsing the input path` {
        private val projectPath = resourceAsPath("/").parent.parent.parent.parent

        @Test
        fun `the current working directory is used if parameter is not set`() {
            val spec = parseArguments(emptyArray()).toSpec()
            assertThat(spec.projectSpec.inputPaths).containsExactly(Path("").absolute())
        }

        @Test
        fun `a single value is converted to a path`() {
            val spec = parseArguments(arrayOf("--input", "$projectPath")).toSpec()
            assertThat(spec.projectSpec.inputPaths).containsExactly(projectPath)
        }

        @Test
        fun `a single value is converted to a path absolute`() {
            val spec = parseArguments(arrayOf("--input", "${projectPath.absolute()}")).toSpec()
            assertThat(spec.projectSpec.inputPaths).containsExactly(projectPath.absolute())
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "src/main,../detekt-core/src/test,build.gradle.kts",
                "src/main;../detekt-core/src/test;build.gradle.kts",
                "src/main,../detekt-core/src/test;build.gradle.kts",
            ]
        )
        fun `when the input is defined it is passed to the spec`(param: String) {
            val spec = parseArguments(arrayOf("--input", param)).toSpec()
            assertThat(spec.projectSpec.inputPaths).containsExactly(
                Path("src/main"),
                Path("../detekt-core/src/test"),
                Path("build.gradle.kts"),
            )
        }

        @Test
        fun `reports an error if the input path does not exist`() {
            val params = arrayOf("--input", "nonExistent ")

            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(params) }
                .withMessage("Input path does not exist: nonExistent ")
        }
    }

    @Nested
    inner class `parsing config parameters` {

        @Test
        fun `should fail on invalid config value`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--config", "sfsjfsdkfsd")).toSpec() }
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
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
                assertThatCode { parseArguments(arrayOf("--baseline", "nonExistent")) }
                    .isInstanceOf(HandledArgumentViolation::class.java)
                    .hasMessageContaining("The file specified by --baseline should exist 'nonExistent'.")
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

    @Test
    fun `should load single filter`() {
        val filters = CliArgs { excludes = "**/one/**" }.toSpecFilters()
        assertThat(filters?.isIgnored(Path("/one/path"))).isTrue()
        assertThat(filters?.isIgnored(Path("/two/path"))).isFalse()
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

    @Test
    fun `base-path with a non existent directory`() {
        assertThatExceptionOfType(HandledArgumentViolation::class.java)
            .isThrownBy { parseArguments(arrayOf("--base-path", "nonExistent")) }
            .withMessage("Value passed to --base-path must be a directory.")
    }

    @Test
    fun `jdk-home with a non existent directory`() {
        assertThatExceptionOfType(HandledArgumentViolation::class.java)
            .isThrownBy { parseArguments(arrayOf("--jdk-home", "nonExistent")) }
            .withMessage("Value passed to --jdk-home must be a directory.")
    }
}

private fun CliArgs.toSpecFilters(): PathFilters? {
    val spec = this.createSpec(NullPrintStream(), NullPrintStream()).projectSpec
    return PathFilters.of(spec.includes.toList(), spec.excludes.toList())
}
