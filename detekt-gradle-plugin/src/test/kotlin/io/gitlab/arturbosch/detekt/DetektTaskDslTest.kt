package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object DetektTaskDslTest : Spek({

    describe("When applying the detekt gradle plugin") {
        lateinit var gradleRunner: DslGradleRunner
        lateinit var result: BuildResult

        listOf(groovy().dryRun(), kotlin().dryRun()).forEach { builder ->
            context("using ${builder.gradleBuildName}") {
                describe("without detekt config") {

                    beforeGroup {
                        gradleRunner = builder.build()
                        result = gradleRunner.runDetektTask()
                    }

                    it("completes successfully") {
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    }

                    it("enables xml report to default location") {
                        val xmlReportFile = gradleRunner.projectFile("build/reports/detekt/detekt.xml")
                        assertThat(result.output).contains("--report xml:$xmlReportFile")
                    }

                    it("enables html report to default location") {
                        val htmlReportFile = gradleRunner.projectFile("build/reports/detekt/detekt.html")
                        assertThat(result.output).contains("--report html:$htmlReportFile")
                    }

                    it("enables text report to default location") {
                        val textReportFile = gradleRunner.projectFile("build/reports/detekt/detekt.txt")
                        assertThat(result.output).contains("--report txt:$textReportFile")
                    }
                }

                describe("without multiple detekt configs") {

                    beforeGroup {
                        val config = """
                        |detekt {
                        |    config.setFrom(files("firstConfig.yml", "secondConfig.yml"))
                        |}
                        """

                        gradleRunner = builder.withDetektConfig(config).build()

                        result = gradleRunner.runDetektTask()
                    }

                    it("passes absolute filename of both config files to detekt cli") {
                        val firstConfig = gradleRunner.projectFile("firstConfig.yml")
                        val secondConfig = gradleRunner.projectFile("secondConfig.yml")

                        val expectedConfigParam = "--config $firstConfig,$secondConfig"
                        assertThat(result.output).contains(expectedConfigParam)
                    }
                }

                describe("with custom baseline file") {
                    val baselineFilename = "detekt-baseline.xml"

                    beforeGroup {

                        val config = """
                        |detekt {
                        |   baseline = file("$baselineFilename")
                        |}
                        """

                        gradleRunner = builder
                            .withDetektConfig(config)
                            .withBaseline(baselineFilename)
                            .build()
                        result = gradleRunner.runDetektTask()
                    }

                    it("sets baseline parameter with absolute filename") {
                        val baselineFile = gradleRunner.projectFile(baselineFilename)
                        val expectedBaselineArgument = "--baseline $baselineFile"
                        assertThat(result.output).contains(expectedBaselineArgument)
                    }
                }

                describe("with custom input directories") {
                    val customSrc1 = "gensrc/kotlin"
                    val customSrc2 = "src/main/kotlin"

                    beforeGroup {

                        val config = """
                        |detekt {
                        |    input = files("$customSrc1", "$customSrc2", "folder_that_does_not_exist")
                        |}
                        """

                        val projectLayout = ProjectLayout(1, srcDirs = listOf(customSrc1, customSrc2))
                        gradleRunner = builder
                            .withProjectLayout(projectLayout)
                            .withDetektConfig(config)
                            .build()
                        result = gradleRunner.runDetektTask()
                    }

                    it("sets input parameter to absolute filenames of all source files") {
                        val file1 = gradleRunner.projectFile("$customSrc1/MyRoot0Class.kt")
                        val file2 = gradleRunner.projectFile("$customSrc2/MyRoot0Class.kt")
                        val expectedInputParam = "--input $file1,$file2"
                        assertThat(result.output).contains(expectedInputParam)
                    }

                    it("ignores input directories that do not exist") {
                        assertThat(result.output).doesNotContain("folder_that_does_not_exist")
                    }
                }

                describe("with custom reports dir") {

                    beforeGroup {

                        val config = """
                        |detekt {
                        |    reportsDir = file("build/detekt-reports")
                        |}
                        """

                        gradleRunner = builder
                            .withDetektConfig(config)
                            .build()
                        result = gradleRunner.runDetektTask()
                    }

                    it("configures xml report to custom directory") {
                        val xmlReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.xml")
                        assertThat(result.output).contains("--report xml:$xmlReportFile")
                    }

                    it("configures html report to custom directory") {
                        val htmlReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.html")
                        assertThat(result.output).contains("--report html:$htmlReportFile")
                    }

                    it("configures text report to custom directory") {
                        val textReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.txt")
                        assertThat(result.output).contains("--report txt:$textReportFile")
                    }
                }

                describe("with custom reports dir and custom report filename") {

                    beforeGroup {

                        val config = """
                        |detekt {
                        |    reportsDir = file("build/detekt-reports")
                        |    reports {
                        |        xml.destination = file("build/xml-reports/custom-detekt.xml")
                        |    }
                        |}
                        """

                        gradleRunner = builder
                            .withDetektConfig(config)
                            .build()
                        result = gradleRunner.runDetektTask()
                    }

                    it("configures xml report to specific absolute filename") {
                        val xmlReportFile = gradleRunner.projectFile("build/xml-reports/custom-detekt.xml")
                        assertThat(result.output).contains("--report xml:$xmlReportFile")
                    }

                    it("configures html report to default name in custom directory") {
                        val htmlReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.html")
                        assertThat(result.output).contains("--report html:$htmlReportFile")
                    }

                    it("configures text report to default name in custom directory") {
                        val textReportFile = gradleRunner.projectFile("build/detekt-reports/detekt.txt")
                        assertThat(result.output).contains("--report txt:$textReportFile")
                    }
                }

                describe("with disabled reports") {

                    beforeGroup {

                        val config = """
                        |detekt {
                        |    reports {
                        |        xml.enabled = false
                        |        html {
                        |            enabled = false
                        |        }
                        |        txt {
                        |            enabled = false
                        |        }
                        |    }
                        |}
                        """

                        gradleRunner = builder
                            .withDetektConfig(config)
                            .build()
                        result = gradleRunner.runDetektTask()
                    }

                    it("no report param is set") {
                        assertThat(result.output).doesNotContain("--report")
                    }
                }

                describe("with custom report types") {
                    describe("configured correctly") {
                        beforeGroup {

                            val config = """
                                |detekt {
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

                            gradleRunner = builder.withDetektConfig(config).build()
                            result = gradleRunner.runDetektTask()
                        }

                        it("configures custom xml report to absolute filename") {
                            val xmlReportFile = gradleRunner.projectFile("build/reports/custom.xml")
                            assertThat(result.output).contains("--report customXml:$xmlReportFile")
                        }

                        it("configures custom json report to absolute filename") {
                            val xmlReportFile = gradleRunner.projectFile("build/reports/custom.json")
                            assertThat(result.output).contains("--report customJson:$xmlReportFile")
                        }
                    }

                    describe("report id is missing") {
                        beforeGroup {

                            val config = """
                                |detekt {
                                |    reports {
                                |        custom {
                                |           destination = file("build/reports/custom.xml")
                                |       }
                                |    }
                                |}
                                """

                            gradleRunner = builder.withDetektConfig(config).build()
                        }

                        it("fails the build") {
                            gradleRunner.runDetektTaskAndExpectFailure()
                        }
                    }

                    describe("report filename is missing") {
                        beforeGroup {

                            val config = """
                                |detekt {
                                |    reports {
                                |        custom {
                                |           reportId = "customJson"
                                |       }
                                |    }
                                |}
                                """

                            gradleRunner = builder.withDetektConfig(config).build()
                        }

                        it("fails the build") {
                            gradleRunner.runDetektTaskAndExpectFailure()
                        }
                    }

                    describe("report filename is a directory") {
                        beforeGroup {

                            val aDirectory = "\${rootDir}/src"

                            val config = """
                                |detekt {
                                |    reports {
                                |        custom {
                                |           reportId = "foo"
                                |           destination = file("$aDirectory")
                                |       }
                                |    }
                                |}
                                """

                            gradleRunner = builder.withDetektConfig(config).build()
                        }

                        it("fails the build") {
                            gradleRunner.runDetektTaskAndExpectFailure()
                        }
                    }

                    describe("using the report id of a well known type") {
                        DetektReportType.values().forEach { wellKnownType ->
                            context(wellKnownType.name) {
                                beforeGroup {

                                    val config = """
                                        |detekt {
                                        |    reports {
                                        |        custom {
                                        |           reportId = "${wellKnownType.reportId}"
                                        |           destination = file("build/reports/custom.xml")
                                        |       }
                                        |    }
                                        |}
                                        """

                                    gradleRunner = builder.withDetektConfig(config).build()
                                }
                            }
                            it("fails the build") {
                                gradleRunner.runDetektTaskAndExpectFailure()
                            }
                        }
                    }
                }

                describe("with flags") {

                    beforeGroup {

                        val config = """
                        |detekt {
                        |    debug = true
                        |    parallel = true
                        |    disableDefaultRuleSets = true
                        |    failFast = true
                        |    autoCorrect = true
                        |    buildUponDefaultConfig = true
                        |    ignoreFailures = true
                        |}
                        """

                        gradleRunner = builder
                            .withDetektConfig(config)
                            .build()
                        result = gradleRunner.runDetektTask()
                    }

                    it("enables debug mode") {
                        assertThat(result.output).contains("--debug")
                    }

                    it("enables parallel processing") {
                        assertThat(result.output).contains("--parallel")
                    }

                    it("disables default ruleset") {
                        assertThat(result.output).contains("--disable-default-rulesets")
                    }

                    it("ignores failures") {
                        assertThat(result.output).contains("Ignore failures: true")
                    }

                    it("enables fail fast") {
                        assertThat(result.output).contains("--fail-fast")
                    }

                    it("enables auto correcting") {
                        assertThat(result.output).contains("--auto-correct")
                    }

                    it("enables using default config as baseline") {
                        assertThat(result.output).contains("--build-upon-default-config")
                    }
                }

                describe("with an additional plugin") {
                    beforeGroup {
                        val config = """
                            |dependencies {
                            |   detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$VERSION_UNDER_TEST")
                            |}
                            """

                        gradleRunner = builder
                            .withDetektConfig(config)
                            .build()
                        result = gradleRunner.runTasks("dependencies", "--configuration", "detektPlugins")
                    }

                    it("successfully checks dependencies") {
                        assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    }

                    it("adds the formatting lib to the project dependencies") {
                        assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-formatting:$VERSION_UNDER_TEST")
                    }
                }

                describe("with a custom tool version") {
                    val customVersion = "1.0.0.RC8"
                    beforeGroup {
                        val config = """
                            |detekt {
                            |    toolVersion = "$customVersion"
                            |}
                            """

                        gradleRunner = builder
                            .withDetektConfig(config)
                            .build()
                        result = gradleRunner.runTasks("dependencies", "--offline", "--configuration", "detekt")
                    }

                    it("successfully checks dependencies") {
                        assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    }

                    it("adds the custom detekt version to the dependencies") {
                        assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-cli:$customVersion")
                    }
                }
            }
        }

        describe("and creating a custom task") {
            context("using the groovy dsl") {
                val builder = groovy().dryRun()
                beforeGroup {
                    val config = """
                        |task detektFailFast(type: io.gitlab.arturbosch.detekt.Detekt) {
                        |    description = "Runs a failfast detekt build."
                        |
                        |    setSource("${"$"}projectDir")
                        |    config.setFrom(files("config.yml"))
                        |    includes = ["**/*.kt", "**/*.kts"]
                        |    excludes = ["build/"]
                        |    debug = true
                        |    parallel = true
                        |    disableDefaultRuleSets = true
                        |    buildUponDefaultConfig = true
                        |    failFast = false
                        |    ignoreFailures = false
                        |    autoCorrect = false
                        |    reports {
                        |        xml {
                        |            enabled = true
                        |            destination = file("build/reports/failfast.xml")
                        |        }
                        |        html.destination = file("build/reports/failfast.html")
                        |        txt.destination = file("build/reports/failfast.txt")
                        |    }
                        |}
                        """

                    gradleRunner = builder
                        .withDetektConfig(config)
                        .build()
                        .apply { writeProjectFile("config.yml", "") }

                    result = gradleRunner.runTasks("detektFailFast")
                }

                it("completes successfully") {
                    assertThat(result.task(":detektFailFast")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                }

                it("enables xml report to specified location") {
                    val xmlReportFile = gradleRunner.projectFile("build/reports/failfast.xml")
                    assertThat(result.output).contains("--report xml:$xmlReportFile")
                }

                it("enables html report to specified location") {
                    val htmlReportFile = gradleRunner.projectFile("build/reports/failfast.html")
                    assertThat(result.output).contains("--report html:$htmlReportFile")
                }

                it("enables text report to specified location") {
                    val textReportFile = gradleRunner.projectFile("build/reports/failfast.txt")
                    assertThat(result.output).contains("--report txt:$textReportFile")
                }

                it("sets absolute filename of both config file to detekt cli") {
                    val config = gradleRunner.projectFile("config.yml")

                    val expectedConfigParam = "--config $config"
                    assertThat(result.output).contains(expectedConfigParam)
                }

                it("enables debug mode") {
                    assertThat(result.output).contains("--debug")
                }

                it("enables parallel processing") {
                    assertThat(result.output).contains("--parallel")
                }

                it("disables the default ruleset") {
                    assertThat(result.output).contains("--disable-default-rulesets")
                }
            }

            context("using the kotlin dsl") {
                val builder = kotlin().dryRun()
                beforeGroup {
                    val config = """
                        |task<io.gitlab.arturbosch.detekt.Detekt>("detektFailFast") {
                        |    description = "Runs a failfast detekt build."
                        |
                        |    setSource(files("${"$"}projectDir"))
                        |    setIncludes(listOf("**/*.kt", "**/*.kts"))
                        |    setExcludes(listOf("build/"))
                        |    config.setFrom(files("config.yml"))
                        |    debug = true
                        |    parallel = true
                        |    disableDefaultRuleSets = true
                        |    buildUponDefaultConfig = true
                        |    failFast = false
                        |    ignoreFailures = false
                        |    autoCorrect = false
                        |    reports {
                        |        xml {
                        |            enabled = true
                        |            destination = file("build/reports/failfast.xml")
                        |        }
                        |        html.destination = file("build/reports/failfast.html")
                        |        txt.destination = file("build/reports/failfast.txt")
                        |    }
                        |}
                        """

                    gradleRunner = builder
                        .withDetektConfig(config)
                        .build()
                        .apply { writeProjectFile("config.yml", "") }

                    result = gradleRunner.runTasks("detektFailFast")
                }

                it("completes successfully") {
                    assertThat(result.task(":detektFailFast")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                }

                it("enables xml report to specified location") {
                    val xmlReportFile = gradleRunner.projectFile("build/reports/failfast.xml")
                    assertThat(result.output).contains("--report xml:$xmlReportFile")
                }

                it("enables html report to specified location") {
                    val htmlReportFile = gradleRunner.projectFile("build/reports/failfast.html")
                    assertThat(result.output).contains("--report html:$htmlReportFile")
                }

                it("enables text report to specified location") {
                    val textReportFile = gradleRunner.projectFile("build/reports/failfast.txt")
                    assertThat(result.output).contains("--report txt:$textReportFile")
                }

                it("sets absolute filename of both config file to detekt cli") {
                    val config = gradleRunner.projectFile("config.yml")

                    val expectedConfigParam = "--config $config"
                    assertThat(result.output).contains(expectedConfigParam)
                }

                it("enables debug mode") {
                    assertThat(result.output).contains("--debug")
                }

                it("enables parallel processing") {
                    assertThat(result.output).contains("--parallel")
                }

                it("disables the default ruleset") {
                    assertThat(result.output).contains("--disable-default-rulesets")
                }
            }
        }
    }
})
