package dev.detekt.cli

import dev.detekt.api.Severity
import dev.detekt.test.utils.NullPrintStream
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.AnalysisMode
import dev.detekt.tooling.api.spec.RulesSpec.FailurePolicy.FailOnSeverity
import dev.detekt.tooling.api.spec.RulesSpec.FailurePolicy.NeverFail
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.Parameter
import org.junit.jupiter.params.ParameterizedClass
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.writeText

internal class CliArgsSpec {

    @Nested
    inner class `Parsing the input path` {
        private val pathBuildGradle = Path("build.gradle.kts").absolute()
        private val pathCliArgs = Path("src/main/kotlin/dev/detekt/cli/CliArgs.kt").absolute()
        private val pathCliArgsSpec = Path("src/test/kotlin/dev/detekt/cli/CliArgsSpec.kt").absolute()
        private val pathAnalyzer =
            Path("../detekt-core/src/test/kotlin/dev/detekt/core/AnalyzerSpec.kt").absolute()
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

        @Test
        fun `when the input is defined it is passed to the spec`() {
            val spec = parseArguments(
                arrayOf(
                    "--input",
                    "src/main${File.pathSeparator}../detekt-core/src/test${File.pathSeparator}build.gradle.kts",
                )
            ).toSpec()

            assertThat(spec.projectSpec.inputPaths).contains(pathBuildGradle)
            assertThat(spec.projectSpec.inputPaths).contains(pathCliArgs)
            assertThat(spec.projectSpec.inputPaths).doesNotContain(pathCliArgsSpec)
            assertThat(spec.projectSpec.inputPaths).contains(pathAnalyzer)
        }

        @Test
        fun `reports an error if the input path does not exist`() {
            val params = arrayOf("--input", "nonExistent")

            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(params) }
                .withMessage("path \"nonExistent\" does not exist.")
        }

        @Nested
        inner class FilterInput {
            private val input = arrayOf(
                "--input",
                "src/main/../main/${File.pathSeparator}../detekt-core/src/test${File.pathSeparator}build.gradle.kts",
            )
            private val pathMain = Path("src/main/kotlin/dev/detekt/cli/Main.kt").absolute()

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

            @ParameterizedTest
            @ValueSource(strings = ["**/test/**,*.kts", "**/test/**;*.kts"])
            fun `multiples excludes in path`(filter: String) {
                val spec = parseArguments(input + arrayOf("--excludes", filter)).toSpec()

                assertThat(spec.projectSpec.inputPaths).doesNotContain(pathBuildGradle)
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

            @ParameterizedTest
            @ValueSource(strings = ["**/test/**,*.kts", "**/test/**;*.kts"])
            fun `multiples includes in path`(filter: String) {
                val spec = parseArguments(input + arrayOf("--includes", filter)).toSpec()

                assertThat(spec.projectSpec.inputPaths).contains(pathBuildGradle)
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

            @ParameterizedTest
            @ValueSource(strings = ["/home/**", "/Users/**"])
            fun `doesn't take into account absolute path`(glob: String) {
                val spec = parseArguments(input + arrayOf("--excludes", glob)).toSpec()

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

            @Nested
            @ParameterizedClass
            @ValueSource(strings = ["--excludes", "--includes"])
            inner class Validate {
                @Parameter
                lateinit var arg: String

                @Test
                fun spaceEnd() {
                    assertThatExceptionOfType(HandledArgumentViolation::class.java)
                        .isThrownBy { parseArguments(input + arrayOf(arg, "**/main/** ")) }
                }

                @Test
                fun spaceStart() {
                    assertThatExceptionOfType(HandledArgumentViolation::class.java)
                        .isThrownBy { parseArguments(input + arrayOf(arg, " **/main/**")) }
                }

                @Test
                fun empty() {
                    assertThatExceptionOfType(HandledArgumentViolation::class.java)
                        .isThrownBy { parseArguments(input + arrayOf(arg, "")) }
                }

                @Test
                fun blank() {
                    assertThatExceptionOfType(HandledArgumentViolation::class.java)
                        .isThrownBy { parseArguments(input + arrayOf(arg, " ")) }
                }
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
                assertThat(spec.projectSpec.analysisMode).isEqualTo(AnalysisMode.Light)
            }

            @Test
            fun `--analysis-mode full is accepted`() {
                val spec = parseArguments(arrayOf("--analysis-mode", "full")).toSpec()
                assertThat(spec.projectSpec.analysisMode).isEqualTo(AnalysisMode.Full)
            }

            @Test
            fun `throws exception on invalid analysis mode`() {
                assertThatExceptionOfType(HandledArgumentViolation::class.java)
                    .isThrownBy { parseArguments(arrayOf("--analysis-mode", "invalid")) }
                    .withMessage("invalid choice: invalid. (choose from full, light)")
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["-h", "--help"])
        fun `throws HelpRequest on help flag`(helpFlag: String) {
            assertThatThrownBy { parseArguments(arrayOf(helpFlag)) }
                .isInstanceOf(HelpRequest::class.java)
                .extracting { (it as HelpRequest).usageText.trim() }
                .isEqualTo(expectedHelp)
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
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--jvm-target", "2")) }
                .withMessageStartingWith("Invalid value passed to --jvm-target, expected one of [1.6, 1.8, 9, 10, 11, ")
        }

        @Test
        fun `supported --api-version is accepted`() {
            val spec = parseArguments(arrayOf("--api-version", "2.5")).toSpec()
            assertThat(spec.compilerSpec.apiVersion).isEqualTo("2.5")
        }

        @Test
        fun `unsupported --api-version returns error message`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--api-version", "1.9")) }
                .withMessageStartingWith("\"1.9\" passed to --api-version, expected one of [2.0, 2.1, 2.2, 2.3, 2.4, ")
        }

        @Test
        fun `invalid --api-version returns error message`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--api-version", "x")) }
                .withMessageStartingWith("\"x\" passed to --api-version, expected one of [2.0, 2.1, 2.2, 2.3, 2.4, ")
        }

        @Test
        fun `supported --language-version is accepted`() {
            val spec = parseArguments(arrayOf("--language-version", "2.5")).toSpec()
            assertThat(spec.compilerSpec.languageVersion).isEqualTo("2.5")
        }

        @Test
        fun `unsupported --language-version returns error message`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--language-version", "1.9")) }
                .withMessageStartingWith(
                    "\"1.9\" passed to --language-version, expected one of [2.0, 2.1, 2.2, 2.3, 2.4, "
                )
        }

        @Test
        fun `invalid --language-version returns error message`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--language-version", "x")) }
                .withMessageStartingWith(
                    "\"x\" passed to --language-version, expected one of [2.0, 2.1, 2.2, 2.3, 2.4, "
                )
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

            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(args) }
                .withMessage("'foo' is not a valid FailureSeverity. Allowed values are [Error, Warning, Info, Never]")
        }
    }

    @Test
    fun `base-path with a non existent directory`() {
        assertThatExceptionOfType(HandledArgumentViolation::class.java)
            .isThrownBy { parseArguments(arrayOf("--base-path", "nonExistent")) }
            .withMessage("directory \"nonExistent\" does not exist.")
    }

    @Test
    fun `jdk-home with a non existent directory`() {
        assertThatExceptionOfType(HandledArgumentViolation::class.java)
            .isThrownBy { parseArguments(arrayOf("--jdk-home", "nonExistent")) }
            .withMessage("directory \"nonExistent\" does not exist.")
    }

    @Nested
    inner class Reports {
        @Test
        fun `fails when there is no separator`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--report", "foo")) }
                .withMessage(
                    "Input 'foo' must consist of two parts (report-id:path)."
                )
        }

        @Test
        fun `fails when empty`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--report", " ")).reportPaths }
                .withMessage(
                    "Input ' ' must consist of two parts (report-id:path)."
                )
        }

        @Test
        fun `fails when there is no id`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--report", ":foo")) }
                .withMessage("The kind of report must not be empty (path - foo)")
        }

        @Test
        fun `fails when there is no path`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("--report", "foo:")) }
                .withMessage("The path of the report must not be empty (kind - foo)")
        }

        @Test
        fun `works when there is separator`() {
            val args = parseArguments(arrayOf("--report", "foo:bar"))

            assertThat(args.reportPaths).isEqualTo(listOf(ReportPath("foo", Path("bar"))))
        }

        @Test
        fun `works when with two reports`() {
            val args = parseArguments(arrayOf("--report", "foo:bar", "-r", "abc:asdf"))

            assertThat(args.reportPaths)
                .isEqualTo(listOf(ReportPath("foo", Path("bar")), ReportPath("abc", Path("asdf"))))
        }
    }

    @Nested
    inner class ArgumentFiles {
        @Test
        fun `parses argument file`(@TempDir tempDir: Path) {
            val argFile = tempDir.resolve("args.txt")
            argFile.writeText("--all-rules\n--debug")
            val actual = parseArguments(arrayOf("@$argFile"))
            assertThat(actual.allRules).isTrue()
            assertThat(actual.debug).isTrue()
        }

        @Test
        fun `throws when argument file does not exist`() {
            assertThatExceptionOfType(HandledArgumentViolation::class.java)
                .isThrownBy { parseArguments(arrayOf("@nonExistent")) }
        }
    }
}

private fun CliArgs.toSpec() = createSpec(NullPrintStream(), NullPrintStream())

private val expectedHelp = """
    |Usage: detekt [<options>] [<freecompilerargs>]...
    |
    |Options:
    |  -i, --input=<path>               Input paths to analyze. Multiple paths are separated by ':' on *nix or ';' on Windows. If not specified the current working directory is used.
    |  --analysis-mode=(full|light)     Analysis mode used by detekt. 'full' analysis mode is comprehensive but requires the correct compiler options to be provided. 'light' analysis cannot utilise compiler information and some rules cannot be run in this mode.
    |  -in, --includes=<text>           Globbing patterns describing paths to include in the analysis. Useful in combination with 'excludes' patterns.
    |  -ex, --excludes=<text>           Globbing patterns describing paths to exclude from the analysis.
    |  -c, --config=<path>              Path to the config file (path/to/config.yml). Multiple configuration files can be specified with ':' on *nix or ';' on Windows as separator.
    |  -cr, --config-resource=<value>   Path to the config resource on detekt's classpath (path/to/config.yml).
    |  -gc, --generate-config=<path>    Export default config to the provided path.
    |  -p, --plugins=<path>             Extra paths to plugin jars separated by ':' on *nix or ';' on Windows.
    |  --parallel                       Enables parallel compilation and analysis of source files. Do some benchmarks first before enabling this flag. Heuristics show performance benefits starting from 2000 lines of Kotlin code.
    |  -b, --baseline=<path>            If a baseline xml file is passed in, only new findings not in the baseline are printed in the console.
    |  -cb, --create-baseline           Treats current analysis findings as a smell baseline for future detekt runs.
    |  -r, --report=<value>             Generates a report for given 'report-id' and stores it on given 'path'. Entry should consist of: [report-id:path]. Available 'report-id' values: 'checkstyle', 'html', 'md', 'sarif'. These can also be used in combination with each other e.g. '-r html:reports/detekt.html -r checkstyle:reports/detekt.xml'
    |  --fail-on-severity=<value>       Specifies the minimum severity that causes the build to fail. When the value is set to 'Never' detekt will not fail regardless of the number of issues and their severities.
    |  -bp, --base-path=<path>          Specifies a directory as the base path. Currently it impacts all file paths in the formatted reports. File paths in console output are not affected and remain as absolute paths.
    |  -dd, --disable-default-rulesets  Disables default rule sets.
    |  --build-upon-default-config      Preconfigures detekt with a bunch of rules and some opinionated defaults for you. Allows additional provided configurations to override the defaults.
    |  --all-rules                      Activates all available (even unstable) rules.
    |  -ac, --auto-correct              Allow rules to auto correct code if they support it. The default rule sets do NOT support auto correcting and won't change any line in the users code base. However custom rules can be written to support auto correcting. The additional 'ktlint' rule set, added with '--plugins', does support it and needs this flag.
    |  --debug                          Prints extra information about configurations and extensions.
    |  -cp, --classpath=<path>          Paths where to find user class files and depending jar files. Used for type resolution.
    |  --api-version=<value>            Kotlin API version used by the code under analysis. Some rules use this information to provide more specific rule violation messages.
    |  --language-version=<value>       Compatibility mode for Kotlin language version X.Y, reports errors for all language features that came out later
    |  --jvm-target=<value>             Target version of the generated JVM bytecode that was generated during compilation and is now being used for type resolution
    |  --jdk-home=<path>                Use a custom JDK home directory to include into the classpath
    |  --version                        Prints the detekt CLI version.
    |  -h, --help                       Show this message and exit
    |
    |Arguments:
    |  <freecompilerargs>  Options to pass to the Kotlin compiler.
""".trimMargin()
