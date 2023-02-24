package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import io.gitlab.arturbosch.detekt.extensions.loadDetektVersion
import io.gitlab.arturbosch.detekt.testkit.DslTestBuilder.Companion.kotlin
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class DetektTaskDslSpec {

    val defaultDetektVersion = loadDetektVersion(DetektTaskDslSpec::class.java.classLoader)

    @Nested
    inner class `without detekt config` {
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder.build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `completes successfully`() {
            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `enables xml report to default location`() {
            val xmlReportFile = gradleRunner.projectFile("build/reports/detekt/detekt.xml")
            assertThat(result.output).contains("--report xml:$xmlReportFile")
        }

        @Test
        fun `enables html report to default location`() {
            val htmlReportFile = gradleRunner.projectFile("build/reports/detekt/detekt.html")
            assertThat(result.output).contains("--report html:$htmlReportFile")
        }

        @Test
        fun `enables text report to default location`() {
            val textReportFile = gradleRunner.projectFile("build/reports/detekt/detekt.txt")
            assertThat(result.output).contains("--report txt:$textReportFile")
        }

        @Test
        fun `enables sarif report to default location`() {
            val sarifReportFile = gradleRunner.projectFile("build/reports/detekt/detekt.sarif")
            assertThat(result.output).contains("--report sarif:$sarifReportFile")
        }

        @Test
        @DisplayName("set as input all the kotlin files in src/main/java and src/main/kotlin")
        fun setInputFiles() {
            val file1 = gradleRunner.projectFile("src/main/java/My0Root0Class.kt")
            val file2 = gradleRunner.projectFile("src/test/java/My1Root0Class.kt")
            val file3 = gradleRunner.projectFile("src/main/kotlin/My2Root0Class.kt")
            val file4 = gradleRunner.projectFile("src/test/kotlin/My3Root0Class.kt")
            assertThat(result.output).contains("--input $file1,$file2,$file3,$file4 ")
        }
    }

    @Nested
    inner class `without multiple detekt configs` {
        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |    config.setFrom(files("firstConfig.yml", "secondConfig.yml"))
            |}
        """
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder.withDetektConfig(config).build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `passes absolute filename of both config files to detekt cli`() {
            val firstConfig = gradleRunner.projectFile("firstConfig.yml")
            val secondConfig = gradleRunner.projectFile("secondConfig.yml")

            val expectedConfigParam = "--config $firstConfig,$secondConfig"
            assertThat(result.output).contains(expectedConfigParam)
        }
    }

    @Nested
    inner class `with custom baseline file` {
        val baselineFilename = "custom-baseline.xml"

        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |   baseline = file("$baselineFilename")
            |}
        """
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder
            .withDetektConfig(config)
            .withBaseline(baselineFilename)
            .build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `sets baseline parameter with absolute filename`() {
            val baselineFile = gradleRunner.projectFile(baselineFilename)
            val expectedBaselineArgument = "--baseline $baselineFile"
            assertThat(result.output).contains(expectedBaselineArgument)
        }
    }

    @Nested
    inner class `with custom baseline file that doesn't exist` {
        val baselineFilename = "detekt-baseline-no-exist.xml"

        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |   baseline = file("$baselineFilename")
            |}
        """
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder
            .withDetektConfig(config)
            .build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `doesn't set the baseline parameter`() {
            assertThat(result.output).doesNotContain("--baseline")
        }
    }

    @Nested
    @DisplayName("[deprecated] with custom input directories using input")
    inner class CustomInputDirectoriesUsingInput {
        val customSrc1 = "gensrc/kotlin"
        val customSrc2 = "src/main/kotlin"
        private val builder = kotlin().dryRun()

        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |    input = files("$customSrc1", "$customSrc2", "folder_that_does_not_exist")
            |}
        """

        val projectLayout = ProjectLayout(1, srcDirs = listOf(customSrc1, customSrc2))
        private val gradleRunner = builder
            .withProjectLayout(projectLayout)
            .withDetektConfig(config)
            .build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `sets input parameter to absolute filenames of all source files`() {
            val file1 = gradleRunner.projectFile("$customSrc1/My0Root0Class.kt")
            val file2 = gradleRunner.projectFile("$customSrc2/My1Root0Class.kt")
            val expectedInputParam = "--input $file1,$file2"
            assertThat(result.output).contains(expectedInputParam)
        }

        @Test
        fun `ignores input directories that do not exist`() {
            assertThat(result.output).doesNotContain("folder_that_does_not_exist")
        }
    }

    @Nested
    inner class `with custom input directories` {
        val customSrc1 = "gensrc/kotlin"
        val customSrc2 = "src/main/kotlin"
        private val builder = kotlin().dryRun()

        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |    source = files("$customSrc1", "$customSrc2", "folder_that_does_not_exist")
            |}
        """

        private val projectLayout = ProjectLayout(1, srcDirs = listOf(customSrc1, customSrc2))
        private val gradleRunner = builder
            .withProjectLayout(projectLayout)
            .withDetektConfig(config)
            .build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `sets input parameter to absolute filenames of all source files`() {
            val file1 = gradleRunner.projectFile("$customSrc1/My0Root0Class.kt")
            val file2 = gradleRunner.projectFile("$customSrc2/My1Root0Class.kt")
            val expectedInputParam = "--input $file1,$file2"
            assertThat(result.output).contains(expectedInputParam)
        }

        @Test
        fun `ignores input directories that do not exist`() {
            assertThat(result.output).doesNotContain("folder_that_does_not_exist")
        }
    }

    @Nested
    inner class `with custom reports dir` {
        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |    reportsDir = file("build/detekt-reports")
            |}
        """
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder.withDetektConfig(config).build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `configures xml report to custom directory`() {
            val xmlReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.xml")
            assertThat(result.output).contains("--report xml:$xmlReportFile")
        }

        @Test
        fun `configures html report to custom directory`() {
            val htmlReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.html")
            assertThat(result.output).contains("--report html:$htmlReportFile")
        }

        @Test
        fun `configures text report to custom directory`() {
            val textReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.txt")
            assertThat(result.output).contains("--report txt:$textReportFile")
        }

        @Test
        fun `configures sarif report to custom directory`() {
            val sarifReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.sarif")
            assertThat(result.output).contains("--report sarif:$sarifReportFile")
        }
    }

    @Nested
    inner class `with custom reports dir and custom report filename` {
        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |    reportsDir = file("build/detekt-reports")
            |}
            |
            |tasks.detekt {
            |    reports {
            |        xml.destination = file("build/xml-reports/custom-detekt.xml")
            |    }
            |}
        """
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder.withDetektConfig(config).build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `configures xml report to specific absolute filename`() {
            val xmlReportFile = gradleRunner.projectFile("build/xml-reports/custom-detekt.xml")
            assertThat(result.output).contains("--report xml:$xmlReportFile")
        }

        @Test
        fun `configures html report to default name in custom directory`() {
            val htmlReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.html")
            assertThat(result.output).contains("--report html:$htmlReportFile")
        }

        @Test
        fun `configures text report to default name in custom directory`() {
            val textReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.txt")
            assertThat(result.output).contains("--report txt:$textReportFile")
        }
    }

    @Nested
    inner class `with disabled reports` {
        @Suppress("TrimMultilineRawString")
        private val config = """
            |tasks.detekt {
            |    reports {
            |        xml.enabled = false
            |        html {
            |            enabled = false
            |        }
            |        txt {
            |            enabled = false
            |        }
            |        sarif {
            |            enabled = false
            |        }
            |        md {
            |            enabled = false
            |        }
            |    }
            |}
        """
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder.withDetektConfig(config).build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `no report param is set`() {
            assertThat(result.output).doesNotContain("--report")
        }
    }

    @Nested
    inner class `with custom report types` {
        @Nested
        inner class `configured correctly` {
            @Suppress("TrimMultilineRawString")
            private val config = """
                |tasks.detekt {
                |    reports {
                |        custom {
                |           reportId = "customXml"
                |           destination = file("build/reports/custom.xml")
                |       }
                |        custom {
                |           reportId = "customJson"
                |           destination = file("build/reports/custom.json")
                |       }
                |    }
                |}
            """
            private val builder = kotlin().dryRun()
            private val gradleRunner = builder.withDetektConfig(config).build()
            private val result = gradleRunner.runDetektTask()

            @Test
            fun `configures custom xml report to absolute filename`() {
                val xmlReportFile = gradleRunner.projectFile("build/reports/custom.xml")
                assertThat(result.output).contains("--report customXml:$xmlReportFile")
            }

            @Test
            fun `configures custom json report to absolute filename`() {
                val xmlReportFile = gradleRunner.projectFile("build/reports/custom.json")
                assertThat(result.output).contains("--report customJson:$xmlReportFile")
            }
        }

        @Nested
        inner class `report id is missing` {
            @Suppress("TrimMultilineRawString")
            private val config = """
                |tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                |    reports {
                |        custom {
                |           destination = file("build/reports/custom.xml")
                |       }
                |    }
                |}
            """
            private val builder = kotlin().dryRun()
            private val gradleRunner = builder.withDetektConfig(config).build()

            @Test
            fun `fails the build`() {
                gradleRunner.runDetektTaskAndExpectFailure { result ->
                    assertThat(result.output)
                        .contains("If a custom report is specified, the reportId must be present")
                }
            }
        }

        @Nested
        inner class `report filename is missing` {
            @Suppress("TrimMultilineRawString")
            private val config = """
                |tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                |    reports {
                |        custom {
                |           reportId = "customJson"
                |       }
                |    }
                |}
            """
            private val builder = kotlin().dryRun()
            private val gradleRunner = builder.withDetektConfig(config).build()

            @Test
            fun `fails the build`() {
                gradleRunner.runDetektTaskAndExpectFailure { result ->
                    assertThat(result.output)
                        .contains("If a custom report is specified, the destination must be present")
                }
            }
        }

        @Nested
        inner class `report filename is a directory` {
            private val aDirectory = "\${rootDir}/src"

            @Suppress("TrimMultilineRawString")
            private val config = """
                |tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                |    reports {
                |        custom {
                |           reportId = "foo"
                |           destination = file("$aDirectory")
                |       }
                |    }
                |}
            """
            private val builder = kotlin().dryRun()
            private val gradleRunner = builder.withDetektConfig(config).build()

            @Test
            fun `fails the build`() {
                gradleRunner.runDetektTaskAndExpectFailure { result ->
                    assertThat(result.output).contains("Cannot write a file to a location pointing at a directory.")
                }
            }
        }

        @Nested
        inner class `using the report id of a well known type` {
            private val builder = kotlin().dryRun()

            @ParameterizedTest
            @EnumSource(DetektReportType::class)
            fun `fails the build`(wellKnownType: DetektReportType) {
                @Suppress("TrimMultilineRawString")
                val config = """
                    |tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    |    reports {
                    |        custom {
                    |            reportId = "${wellKnownType.reportId}"
                    |            destination = file("build/reports/custom.xml")
                    |        }
                    |    }
                    |}
                """

                val gradleRunner = builder.withDetektConfig(config).build()
                gradleRunner.runDetektTaskAndExpectFailure { result ->
                    assertThat(result.output)
                        .contains("The custom report reportId may not be same as one of the default reports")
                }
            }
        }
    }

    @Nested
    inner class `with flags` {
        private val builder = kotlin().dryRun()

        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |    debug = true
            |    parallel = true
            |    disableDefaultRuleSets = true
            |    allRules = true
            |    autoCorrect = true
            |    buildUponDefaultConfig = true
            |    ignoreFailures = true
            |}
        """

        private val gradleRunner = builder.withDetektConfig(config).build()
        private val result = gradleRunner.runDetektTask()

        @Test
        fun `enables debug mode`() {
            assertThat(result.output).contains("--debug")
        }

        @Test
        fun `enables parallel processing`() {
            assertThat(result.output).contains("--parallel")
        }

        @Test
        fun `disables default ruleset`() {
            assertThat(result.output).contains("--disable-default-rulesets")
        }

        @Test
        fun `ignores failures`() {
            assertThat(result.output).contains("Ignore failures: true")
        }

        @Test
        fun `enables all rules`() {
            assertThat(result.output).contains("--all-rules")
        }

        @Test
        fun `enables auto correcting`() {
            assertThat(result.output).contains("--auto-correct")
        }

        @Test
        fun `enables using default config as baseline`() {
            assertThat(result.output).contains("--build-upon-default-config")
        }
    }

    @Nested
    inner class `with cmdline args` {
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder.build()
        private val result = gradleRunner.runDetektTask("--auto-correct")

        @Test
        fun `enables auto correcting`() {
            assertThat(result.output).containsPattern("""Arguments:[^\r\n]*--auto-correct""")
        }
    }

    @Nested
    inner class `with an additional plugin` {
        @Suppress("TrimMultilineRawString")
        private val config = """
            |dependencies {
            |   detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$defaultDetektVersion")
            |}
        """
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder.withDetektConfig(config).build()
        private val result = gradleRunner.runTasks("dependencies", "--configuration", "detektPlugins")

        @Test
        fun `successfully checks dependencies`() {
            assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `adds the formatting lib to the project dependencies`() {
            assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-formatting:$defaultDetektVersion")
        }
    }

    @Nested
    inner class `with a custom tool version` {
        val customVersion = "1.0.0.RC8"

        @Suppress("TrimMultilineRawString")
        private val config = """
            |detekt {
            |    toolVersion = "$customVersion"
            |}
        """
        private val builder = kotlin().dryRun()
        private val gradleRunner = builder.withDetektConfig(config).build()
        private val result = gradleRunner.runTasks("dependencies", "--offline", "--configuration", "detekt")

        @Test
        fun `successfully checks dependencies`() {
            assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `adds the custom detekt version to the dependencies`() {
            assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-cli:$customVersion")
        }
    }

    @Nested
    inner class `and creating a custom task` {
        private val builder = kotlin().dryRun()

        @Suppress("TrimMultilineRawString")
        private val config = """
            |task<io.gitlab.arturbosch.detekt.Detekt>("myDetekt") {
            |    description = "Runs a custom detekt build."
            |
            |    setSource(files("${"$"}projectDir"))
            |    setIncludes(listOf("**/*.kt", "**/*.kts"))
            |    setExcludes(listOf("build/"))
            |    config.setFrom(files("config.yml"))
            |    debug = true
            |    parallel = true
            |    disableDefaultRuleSets = true
            |    buildUponDefaultConfig = true
            |    allRules = false
            |    ignoreFailures = false
            |    autoCorrect = false
            |    reports {
            |        xml {
            |            enabled = true
            |            destination = file("build/reports/mydetekt.xml")
            |        }
            |        html.destination = file("build/reports/mydetekt.html")
            |        txt.destination = file("build/reports/mydetekt.txt")
            |        sarif {
            |            enabled = true
            |            destination = file("build/reports/mydetekt.sarif")
            |        }
            |    }
            |    basePath = projectDir.toString()
            |}
        """

        private val gradleRunner = builder
            .withDetektConfig(config)
            .build()
            .apply { writeProjectFile("config.yml", "") }

        private val result = gradleRunner.runTasks("myDetekt")

        @Test
        fun `completes successfully`() {
            assertThat(result.task(":myDetekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
        }

        @Test
        fun `enables xml report to specified location`() {
            val xmlReportFile = gradleRunner.projectFile("build/reports/mydetekt.xml")
            assertThat(result.output).contains("--report xml:$xmlReportFile")
        }

        @Test
        fun `enables html report to specified location`() {
            val htmlReportFile = gradleRunner.projectFile("build/reports/mydetekt.html")
            assertThat(result.output).contains("--report html:$htmlReportFile")
        }

        @Test
        fun `enables text report to specified location`() {
            val textReportFile = gradleRunner.projectFile("build/reports/mydetekt.txt")
            assertThat(result.output).contains("--report txt:$textReportFile")
        }

        @Test
        fun `enables sarif report to specified location`() {
            val sarifReportFile = gradleRunner.projectFile("build/reports/mydetekt.sarif")
            assertThat(result.output).contains("--report sarif:$sarifReportFile")
        }

        @Test
        fun `sets base path`() {
            assertThat(result.output).contains("--base-path")
        }

        @Test
        fun `sets absolute filename of both config file to detekt cli`() {
            val config = gradleRunner.projectFile("config.yml")

            val expectedConfigParam = "--config $config"
            assertThat(result.output).contains(expectedConfigParam)
        }

        @Test
        fun `enables debug mode`() {
            assertThat(result.output).contains("--debug")
        }

        @Test
        fun `enables parallel processing`() {
            assertThat(result.output).contains("--parallel")
        }

        @Test
        fun `disables the default ruleset`() {
            assertThat(result.output).contains("--disable-default-rulesets")
        }
    }
}
