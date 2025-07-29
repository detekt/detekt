package io.gitlab.arturbosch.detekt.cli

import dev.detekt.api.Severity
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.AnalysisMode
import io.github.detekt.tooling.api.spec.RulesSpec.FailurePolicy.FailOnSeverity
import io.github.detekt.tooling.api.spec.RulesSpec.FailurePolicy.NeverFail
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute

internal class CliArgsSpec {

    @Nested
    inner class `Parsing the input path` {
        private val pathBuildGradle = Path("build.gradle.kts").absolute()
        private val pathCliArgs = Path("src/main/kotlin/io/gitlab/arturbosch/detekt/cli/CliArgs.kt").absolute()
        private val pathCliArgsSpec = Path("src/test/kotlin/io/gitlab/arturbosch/detekt/cli/CliArgsSpec.kt").absolute()
        private val pathAnalyzer =
            Path("../detekt-core/src/test/kotlin/io/gitlab/arturbosch/detekt/core/AnalyzerSpec.kt").absolute()
                .normalize()

        @Test
        fun `the current working directory is used if parameter is not set`() {
            val spec = parseArguments(emptyArray()).toSpec()
            val workingDir = Path("").absolute()

            assertThat(spec.projectSpec.inputPaths).allSatisfy { it.absolute().startsWith(workingDir) }
            assertThat(spec.projectSpec.inputPaths).contains(pathBuildGradle)
            assertThat(spec.projectSpec.inputPaths).contains(pathCliArgs)
            assertThat(spec.projectSpec.inputPaths).contains(pathCliArgsSpec)
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

            assertThat(spec.projectSpec.inputPaths).contains(pathBuildGradle)
            assertThat(spec.projectSpec.inputPaths).contains(pathCliArgs)
            assertThat(spec.projectSpec.inputPaths).doesNotContain(pathCliArgsSpec)
            assertThat(spec.projectSpec.inputPaths).contains(pathAnalyzer)
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        fun `reports an error if the input path does not exist (non-Windows OS)`() {
            val params = arrayOf("--input", "nonExistent ")

            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(params) }
                .withMessage("Input path does not exist: 'nonExistent '")
        }

        @Test
        @EnabledOnOs(OS.WINDOWS)
        fun `reports an error if the input path does not exist (Windows OS)`() {
            val params = arrayOf("--input", "nonExistent ")

            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(params) }
                .withMessage(""""--input": couldn't convert "nonExistent " to a path""")
        }

        @Nested
        inner class FilterInput {
            private val input = arrayOf("--input", "src/main/../main/,../detekt-core/src/test,build.gradle.kts")
            private val pathMain = Path("src/main/kotlin/io/gitlab/arturbosch/detekt/cli/Main.kt").absolute()

            @Test
            fun `no filters`() {
                val spec = parseArguments(input).toSpec()

                assertThat(spec.projectSpec.inputPaths).contains(pathBuildGradle)
                assertThat(spec.projectSpec.inputPaths).contains(pathCliArgs)
                assertThat(spec.projectSpec.inputPaths).contains(pathMain)
                assertThat(spec.projectSpec.inputPaths).contains(pathAnalyzer)
            }

            @Test
            fun `excludes in path`() {
                val spec = parseArguments(input + arrayOf("--excludes", "**/test/**")).toSpec()

                assertThat(spec.projectSpec.inputPaths).contains(pathBuildGradle)
                assertThat(spec.projectSpec.inputPaths).contains(pathCliArgs)
                assertThat(spec.projectSpec.inputPaths).contains(pathMain)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathAnalyzer)
            }

            @Test
            fun `includes in path`() {
                val spec = parseArguments(input + arrayOf("--includes", "**/test/**")).toSpec()

                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathBuildGradle)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathCliArgs)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathMain)
                assertThat(spec.projectSpec.inputPaths).contains(pathAnalyzer)
            }

            @Test
            fun `excludes and includes in path`() {
                val spec = parseArguments(input + arrayOf("--excludes", "**/test/**", "--includes", "**/test/**"))
                    .toSpec()

                assertThat(spec.projectSpec.inputPaths).isEmpty()
            }

            @Test
            fun `excludes in path normalized`() {
                val spec = parseArguments(input + arrayOf("--excludes", "src/main/kotlin/**")).toSpec()

                assertThat(spec.projectSpec.inputPaths).contains(pathBuildGradle)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathCliArgs)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathMain)
                assertThat(spec.projectSpec.inputPaths).contains(pathAnalyzer)
            }

            @Test
            fun `includes and excludes with overlapping patterns - include specific files`() {
                val spec = parseArguments(input + arrayOf("--includes", "**/*.kt", "--excludes", "**/test/**")).toSpec()

                assertThat(spec.projectSpec.inputPaths).contains(pathCliArgs)
                assertThat(spec.projectSpec.inputPaths).contains(pathMain)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathAnalyzer)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathCliArgsSpec)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathBuildGradle)
            }

            @Test
            fun `includes and excludes with overlapping patterns - path matches both`() {
                val spec = parseArguments(
                    input + arrayOf(
                        "--includes",
                        "**/*.kt",
                        "--excludes",
                        "**/main/**"
                    )
                ).toSpec()

                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathCliArgs)
                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathMain)
                assertThat(spec.projectSpec.inputPaths).contains(pathAnalyzer)
            }

            @Test
            fun `path does not match includes but matches excludes`() {
                val spec = parseArguments(
                    input + arrayOf(
                        "--includes",
                        "**/not_matching/**",
                        "--excludes",
                        "**/test/**"
                    )
                ).toSpec()

                assertThat(spec.projectSpec.inputPaths).isEmpty()
            }

            @Test
            fun `path does not match includes or excludes`() {
                val spec = parseArguments(
                    input + arrayOf(
                        "--includes",
                        "**/not_matching/**",
                        "--excludes",
                        "**/also_not_matching/**"
                    )
                ).toSpec()

                assertThat(spec.projectSpec.inputPaths).isEmpty()
            }

            @Test
            fun `doesn't take into account absolute path`() {
                val spec =
                    parseArguments(input + arrayOf("--excludes", "/home/**,/Users/**")).toSpec()

                assertThat(spec.projectSpec.inputPaths).contains(pathBuildGradle)
                assertThat(spec.projectSpec.inputPaths).contains(pathCliArgs)
                assertThat(spec.projectSpec.inputPaths).contains(pathMain)
                assertThat(spec.projectSpec.inputPaths).contains(pathAnalyzer)
            }

            @Test
            fun `excludes main but includes one file`() {
                val spec = parseArguments(input + arrayOf("--excludes", "**/main/**", "--includes", "**/CliArgs.kt"))
                    .toSpec()

                assertThat(spec.projectSpec.inputPaths).isEmpty()
            }

            @Test
            fun `parse excludes correctly`() {
                val paths: List<Collection<Path>> = listOf(
                    "**/main/**,**/detekt-core/**,**/build.gradle.kts",
                    "**/main/**;**/detekt-core/**;**/build.gradle.kts",
                    "**/main/** ,**/detekt-core/**, **/build.gradle.kts",
                    "**/main/** ;**/detekt-core/**; **/build.gradle.kts",
                    "**/main/**,**/detekt-core/**;**/build.gradle.kts",
                    "**/main/** ,**/detekt-core/**; **/build.gradle.kts",
                    " ,,**/main/**,**/detekt-core/**,**/build.gradle.kts",
                ).map {
                    val spec = parseArguments(input + arrayOf("--excludes", it)).toSpec()
                    spec.projectSpec.inputPaths
                }

                assertThat(paths.distinct()).hasSize(1)
            }

            @Test
            fun `parse includes correctly`() {
                val paths: List<Collection<Path>> = listOf(
                    "**/main/**,**/detekt-core/**,**/build.gradle.kts",
                    "**/main/**;**/detekt-core/**;**/build.gradle.kts",
                    "**/main/** ,**/detekt-core/**, **/build.gradle.kts",
                    "**/main/** ;**/detekt-core/**; **/build.gradle.kts",
                    "**/main/**,**/detekt-core/**;**/build.gradle.kts",
                    "**/main/** ,**/detekt-core/**; **/build.gradle.kts",
                    " ,,**/main/**,**/detekt-core/**,**/build.gradle.kts",
                ).map {
                    val spec = parseArguments(input + arrayOf("--includes", it)).toSpec()
                    spec.projectSpec.inputPaths
                }

                assertThat(paths.distinct()).hasSize(1)
            }
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
                    .hasMessage("Creating a baseline.xml requires the --baseline parameter to specify a path.")
            }

            @Test
            fun `reports an error when using --baseline file does not exist`() {
                assertThatCode { parseArguments(arrayOf("--baseline", "nonExistent")) }
                    .isInstanceOf(HandledArgumentViolation::class.java)
                    .hasMessage("The file specified by --baseline should exist 'nonExistent'.")
            }

            @Test
            fun `reports an error when using --baseline file which is not a file`() {
                val directory = resourceAsPath("/cases").toString()
                assertThatCode { parseArguments(arrayOf("--baseline", directory)) }
                    .isInstanceOf(HandledArgumentViolation::class.java)
                    .hasMessage("The path specified by --baseline should be a file '$directory'.")
            }
        }

        @Nested
        inner class `analysis mode` {

            @Test
            fun `--analysis-mode light is accepted`() {
                val spec = parseArguments(arrayOf("--analysis-mode", "light")).toSpec()
                assertThat(spec.projectSpec.analysisMode).isEqualTo(AnalysisMode.light)
            }

            @Test
            fun `--analysis-mode full is accepted`() {
                val spec = parseArguments(arrayOf("--analysis-mode", "full")).toSpec()
                assertThat(spec.projectSpec.analysisMode).isEqualTo(AnalysisMode.full)
            }

            @Test
            fun `throws exception on invalid analysis mode`() {
                assertThatExceptionOfType(HandledArgumentViolation::class.java)
                    .isThrownBy { parseArguments(arrayOf("--analysis-mode", "invalid")) }
            }
        }

        @Test
        fun `throws HelpRequest on --help`() {
            assertThatExceptionOfType(HelpRequest::class.java)
                .isThrownBy { parseArguments(arrayOf("--help")) }
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
        fun `invalid --jvm-target returns error message`() {
            assertThatIllegalStateException()
                .isThrownBy { parseArguments(arrayOf("--jvm-target", "2")) }
                .withMessageStartingWith("Invalid value passed to --jvm-target, expected one of [1.6, 1.8, 9, 10, 11, ")
        }

        @Test
        fun `--api-version is accepted`() {
            val spec = parseArguments(arrayOf("--api-version", "1.9")).toSpec()
            assertThat(spec.compilerSpec.apiVersion).isEqualTo("1.9")
        }

        @Test
        fun `invalid --api-version returns error message`() {
            assertThatIllegalArgumentException()
                .isThrownBy { parseArguments(arrayOf("--api-version", "0.1")) }
                .withMessageStartingWith("\"0.1\" passed to --api-version, expected one of [1.0, 1.1, 1.2, 1.3, 1.4, ")
        }

        @Test
        fun `--language-version is accepted`() {
            val spec = parseArguments(arrayOf("--language-version", "1.6")).toSpec()
            assertThat(spec.compilerSpec.languageVersion).isEqualTo("1.6")
        }

        @Test
        fun `invalid --language-version returns error message`() {
            assertThatIllegalArgumentException()
                .isThrownBy { parseArguments(arrayOf("--language-version", "2")) }
                .withMessageStartingWith("\"2\" passed to --language-version, expected one of [1.0, 1.1, 1.2, 1.3, ")
        }

        @Test
        fun `valid compiler args are accepted`() {
            val args = arrayOf(
                "-Xcontext-receivers",
                "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi",
            )
            val spec = parseArguments(args).toSpec()

            assertThat(spec.compilerSpec.freeCompilerArgs).containsOnly(
                "-Xcontext-receivers",
                "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi",
            )
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
