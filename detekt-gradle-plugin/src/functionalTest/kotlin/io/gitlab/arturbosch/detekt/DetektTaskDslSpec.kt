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
        private val config = """
            detekt {
                config.setFrom(files("firstConfig.yml", "secondConfig.yml"))
            }
        """.trimIndent()
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

        private val config = """
            detekt {
                baseline = file("$baselineFilename")
            }
        """.trimIndent()
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

        private val config = """
            detekt {
                baseline = file("$baselineFilename")
            }
        """.trimIndent()
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
    inner class `with custom input directories` {
        val customSrc1 = "gensrc/kotlin"
        val customSrc2 = "src/main/kotlin"
        private val builder = kotlin().dryRun()

        private val config = """
            detekt {
                source.setFrom(files("$customSrc1", "$customSrc2", "folder_that_does_not_exist"))
            }
        """.trimIndent()

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
        private val config = """
            detekt {
                reportsDir = file("build/detekt-reports")
            }
        """.trimIndent()
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
        fun `configures sarif report to custom directory`() {
            val sarifReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.sarif")
            assertThat(result.output).contains("--report sarif:$sarifReportFile")
        }
    }

    @Nested
    inner class `with custom reports dir and custom report filename` {
        private val config = """
            detekt {
                reportsDir = file("build/detekt-reports")
            }
            
            tasks.detekt {
                reports {
                    xml.outputLocation.set(file("build/xml-reports/custom-detekt.xml"))
                }
            }
        """.trimIndent()
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
    }

    @Nested
    inner class `with disabled reports` {
        private val config = """
            tasks.detekt {
                reports {
                    xml.required.set(false)
                    html {
                        required.set(false)
                    }
                    sarif {
                        required.set(false)
                    }
                    md {
                        required.set(false)
                    }
                }
            }
        """.trimIndent()
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
            private val config = """
                tasks.detekt {
                    reports {
                        custom {
                           reportId = "customXml"
                           outputLocation.set(file("build/reports/custom.xml"))
                       }
                        custom {
                           reportId = "customJson"
                           outputLocation.set(file("build/reports/custom.json"))
                       }
                    }
                }
            """.trimIndent()
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
            private val config = """
                tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    reports {
                        custom {
                           outputLocation.set(file("build/reports/custom.xml"))
                       }
                    }
                }
            """.trimIndent()
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
            private val config = """
                tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    reports {
                        custom {
                           reportId = "customJson"
                       }
                    }
                }
            """.trimIndent()
            private val builder = kotlin().dryRun()
            private val gradleRunner = builder.withDetektConfig(config).build()

            @Test
            fun `fails the build`() {
                gradleRunner.runDetektTaskAndExpectFailure { result ->
                    assertThat(result.output)
                        .contains("property 'reports.custom.\$0.outputLocation' doesn't have a configured value")
                }
            }
        }

        @Nested
        inner class `report filename is a directory` {
            private val aDirectory = "\${rootDir}/src"

            private val config = """
                tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                    reports {
                        custom {
                           reportId = "foo"
                           outputLocation.set(file("$aDirectory"))
                       }
                    }
                }
            """.trimIndent()
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
                val config = """
                    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
                        reports {
                            custom {
                                reportId = "${wellKnownType.reportId}"
                                outputLocation.set(file("build/reports/custom.xml"))
                            }
                        }
                    }
                """.trimIndent()

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

        private val config = """
            detekt {
                debug = true
                parallel = true
                disableDefaultRuleSets = true
                allRules = true
                autoCorrect = true
                buildUponDefaultConfig = true
                ignoreFailures = true
            }
        """.trimIndent()

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
        fun `enables using default config as baseline`() {
            assertThat(result.output).contains("--build-upon-default-config")
        }
    }

    @Nested
    inner class FailureSeverity {
        private val builder = kotlin().dryRun()

        @Test
        fun `is set to error by default`() {
            val gradleRunner = builder.withDetektConfig("").build()
            val result = gradleRunner.runDetektTask()
            assertThat(result.output).contains("--fail-on-severity error")
        }

        @Test
        fun `can be configured`() {
            val config = """
                detekt {
                    failOnSeverity = io.gitlab.arturbosch.detekt.extensions.FailOnSeverity.Never
                }
            """.trimIndent()

            val gradleRunner = builder.withDetektConfig(config).build()
            val result = gradleRunner.runDetektTask()
            assertThat(result.output).contains("--fail-on-severity never")
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
        private val config = """
            dependencies {
               detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$defaultDetektVersion")
            }
        """.trimIndent()
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

        private val config = """
            detekt {
                toolVersion = "$customVersion"
            }
        """.trimIndent()
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

        private val config = """
            task<io.gitlab.arturbosch.detekt.Detekt>("myDetekt") {
                description = "Runs a custom detekt build."
            
                setSource(files("${"$"}projectDir"))
                setIncludes(listOf("**/*.kt", "**/*.kts"))
                setExcludes(listOf("build/"))
                config.setFrom(files("config.yml"))
                debug = true
                parallel = true
                disableDefaultRuleSets = true
                buildUponDefaultConfig = true
                allRules = false
                ignoreFailures = false
                failOnSeverity = io.gitlab.arturbosch.detekt.extensions.FailOnSeverity.Error
                autoCorrect = false
                reports {
                    xml {
                        required.set(true)
                        outputLocation.set(file("build/reports/mydetekt.xml"))
                    }
                    html.outputLocation.set(file("build/reports/mydetekt.html"))
                    sarif {
                        required.set(true)
                        outputLocation.set(file("build/reports/mydetekt.sarif"))
                    }
                }
                basePath = projectDir.toString()
            }
        """.trimIndent()

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
