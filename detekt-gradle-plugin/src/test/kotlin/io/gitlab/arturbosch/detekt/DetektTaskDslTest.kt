package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class DetektTaskDslTest : Spek({

    describe("When applying the detekt gradle plugin") {
        listOf(groovy(), kotlin()).forEach { builder ->
            describe("using ${builder.gradleBuildName}") {
                it("can be applied without any configuration using its task name") {

                    val gradleRunner = builder.build()
                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("number of classes: 1")
                        assertThat(projectFile("build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("build/reports/detekt/detekt.html")).exists()
                        assertThat(projectFile("build/reports/detekt/detekt.txt")).exists()
                    }
                }

                it("can be applied without any configuration using the check task") {

                    val gradleRunner = builder.build()
                    gradleRunner.runTasksAndCheckResult("check") { result ->
                        assertThat(result.task(":check")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("number of classes: 1")
                        assertThat(result.output).contains("Ruleset: comments")
                    }
                }

                it("can use a custom tool version") {

                    val customVersion = "1.0.0.RC8"
                    val config = """
                        |detekt {
                        |    toolVersion = "$customVersion"
                        |}
                        """

                    val gradleRunner = builder.withDetektConfig(config).build()
                    gradleRunner.runTasksAndCheckResult("dependencies", "--configuration", "detekt") { result ->
                        assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-cli:$customVersion")
                    }
                }

                it("can be applied with multiple config files") {

                    val config = """
                        |detekt {
                        |    config = files("firstConfig.yml", "secondConfig.yml")
                        |}
                        """

                    val gradleRunner = builder.withDetektConfig(config).build()

                    val firstConfig = gradleRunner.projectFile("firstConfig.yml").apply { createNewFile() }
                    val secondConfig = gradleRunner.projectFile("secondConfig.yml").apply { createNewFile() }

                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        val expectedConfigParam = "--config ${firstConfig.canonicalPath},${secondConfig.canonicalPath}"
                        assertThat(result.output).contains(expectedConfigParam)
                    }
                }

                it("can be applied with custom input directories ignoring non existent") {

                    val customSrc = "gensrc/kotlin"
                    val config = """
                        |detekt {
                        |    input = files("$customSrc", "folder_that_does_not_exist")
                        |}
                        """

                    val gradleRunner = builder
                        .withProjectLayout(ProjectLayout(1, srcDirs = listOf(customSrc)))
                        .withDetektConfig(config)
                        .build()

                    gradleRunner.runDetektTaskAndCheckResult { result ->

                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        val expectedInputParam = "--input ${projectFile(customSrc).canonicalPath}"
                        assertThat(result.output).contains(expectedInputParam)
                        assertThat(result.output).doesNotContain("folder_that_does_not_exist")
                    }
                }

                it("can be applied with classes in multiple custom input directories") {

                    val customSrc1 = "gensrc/kotlin"
                    val customSrc2 = "src/main/kotlin"
                    val config = """
                        |detekt {
                        |    input = files("$customSrc1", "$customSrc2")
                        |}
                        """

                    val projectLayout = ProjectLayout(1, srcDirs = listOf(customSrc1, customSrc2))
                    val gradleRunner = builder
                        .withProjectLayout(projectLayout)
                        .withDetektConfig(config)
                        .build()

                    gradleRunner.runDetektTaskAndCheckResult { result ->

                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        val file1 = projectFile("$customSrc1/MyRoot0Class.kt").canonicalPath
                        val file2 = projectFile("$customSrc2/MyRoot0Class.kt").canonicalPath
                        val expectedInputParam = "--input $file1,$file2"
                        assertThat(result.output).contains(expectedInputParam)
                        assertThat(result.output).contains("number of classes: 2")
                    }
                }

                it("can change the general reports dir") {

                    val config = """
                        |detekt {
                        |    reportsDir = file("build/detekt-reports")
                        |}
                        """

                    val gradleRunner = builder
                        .withDetektConfig(config)
                        .build()

                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(projectFile("build/detekt-reports/detekt.xml")).exists()
                        assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
                        assertThat(projectFile("build/detekt-reports/detekt.txt")).exists()
                    }
                }

                it("can change the general reports dir but overwrite single report") {

                    val config = """
                        |detekt {
                        |    reportsDir = file("build/detekt-reports")
                        |    reports {
                        |        xml.destination = file("build/xml-reports/custom-detekt.xml")
                        |    }
                        |}
                        """

                    val gradleRunner = builder
                        .withDetektConfig(config)
                        .build()

                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(projectFile("build/xml-reports/custom-detekt.xml")).exists()
                        assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
                        assertThat(projectFile("build/detekt-reports/detekt.txt")).exists()
                    }
                }

                it("can disable reports") {

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

                    val gradleRunner = builder
                        .withDetektConfig(config)
                        .build()

                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(projectFile("build/reports/detekt/detekt.xml")).doesNotExist()
                        assertThat(projectFile("build/reports/detekt/detekt.html")).doesNotExist()
                        assertThat(projectFile("build/reports/detekt/detekt.txt")).doesNotExist()
                    }
                }

                it("can change all flags") {

                    val config = """
                        |detekt {
                        |    debug = true
                        |    parallel = true
                        |    disableDefaultRuleSets = true
                        |    ignoreFailures = true
                        |}
                        """

                    val gradleRunner = builder
                        .withDetektConfig(config)
                        .build()

                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("--debug", "--parallel", "--disable-default-rulesets")
                    }
                }

                it("allows setting a baseline file") {

                    val baselineFilename = "detekt-baseline.xml"

                    val config = """
                        |detekt {
                        |    baseline = file("$baselineFilename")
                        |}
                        """

                    val gradleRunner = builder
                        .withDetektConfig(config)
                        .withBaseline(baselineFilename)
                        .build()

                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        val expectedBaselineArgument = "--baseline ${projectFile(baselineFilename).canonicalPath}"
                        assertThat(result.output).contains(expectedBaselineArgument)
                    }
                }
                it("can be used with formatting plugin") {

                    val config = """
                    |dependencies {
                    | detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$VERSION_UNDER_TEST")
                    |}
                        """

                    val gradleRunner = builder
                        .withDetektConfig(config)
                        .build()

                    gradleRunner.runTasksAndCheckResult("dependencies", "--configuration", "detektPlugins") { result ->
                        assertThat(result.task(":dependencies")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("io.gitlab.arturbosch.detekt:detekt-formatting:$VERSION_UNDER_TEST")
                    }
                }

                describe("with custom reports") {
                    it("passes multiple custom report params to cli") {

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

                        val gradleRunner = builder
                            .withDetektConfig(config)
                            .build()

                        val customXmlReportFilePath = gradleRunner.projectFile("build/reports/custom.xml").canonicalPath
                        val customJsonReportFilePath =
                            gradleRunner.projectFile("build/reports/custom.json").canonicalPath

                        gradleRunner.runDetektTaskAndCheckResult { result ->
                            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                            assertThat(result.output).contains("--report customXml:$customXmlReportFilePath")
                            assertThat(result.output).contains("--report customJson:$customJsonReportFilePath")
                        }
                    }
                    it("fails if reportId of custom report is missing") {

                        val config = """
                        |detekt {
                        |    reports {
                        |        custom {
                        |           destination = file("build/reports/custom.xml")
                        |       }
                        |    }
                        |}
                        """

                        val gradleRunner = builder
                            .withDetektConfig(config)
                            .build()

                        gradleRunner.runDetektTaskAndExpectFailure()
                    }
                    it("fails if destination of custom report is missing") {

                        val config = """
                        |detekt {
                        |    reports {
                        |        custom {
                        |           reportId = "foo"
                        |       }
                        |    }
                        |}
                        """

                        val gradleRunner = builder
                            .withDetektConfig(config)
                            .build()

                        gradleRunner.runDetektTaskAndExpectFailure()
                    }

                    it("fails if the destination is a directory") {
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

                        val gradleRunner = builder
                            .withDetektConfig(config)
                            .build()

                        gradleRunner.runDetektTaskAndExpectFailure()
                    }

                    DetektReportType.values().forEach { wellKnownType ->
                        it("fails if reportId of custom report is $wellKnownType") {

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

                            val gradleRunner = builder
                                .withDetektConfig(config)
                                .build()

                            gradleRunner.runDetektTaskAndExpectFailure()
                        }
                    }
                }

                describe("using the ignoreFailures toggle") {
                    val projectLayoutWithTooManyIssues = ProjectLayout(
                        numberOfSourceFilesInRootPerSourceDir = 15,
                        numberOfCodeSmellsInRootPerSourceDir = 15
                    )

                    it("build succeeds with more issues than threshold if enabled") {

                        val config = """
                        |detekt {
                        |   ignoreFailures = true
                        |}
                        """

                        val gradleRunner = builder
                            .withProjectLayout(projectLayoutWithTooManyIssues)
                            .withDetektConfig(config)
                            .build()

                        gradleRunner.runDetektTaskAndCheckResult { result ->
                            assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        }
                    }
                    it("build fails with more issues than threshold successfully if disabled") {

                        val config = """
                        |detekt {
                        |   ignoreFailures = false
                        |}
                        """

                        val gradleRunner = builder
                            .withProjectLayout(projectLayoutWithTooManyIssues)
                            .withDetektConfig(config)
                            .build()

                        gradleRunner.runDetektTaskAndExpectFailure()
                    }
                }
            }
        }

        describe("and creating a custom task") {
            it("can be done using the groovy dsl") {

                val config = """
                    |task detektFailFast(type: io.gitlab.arturbosch.detekt.Detekt) {
                    |    description = "Runs a failfast detekt build."
                    |
                    |    input = files("${"$"}projectDir")
                    |    config = files("config.yml")
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

                val gradleRunner = groovy().withDetektConfig(config).build()
                gradleRunner.writeProjectFile("config.yml", "")

                gradleRunner.runTasksAndCheckResult("detektFailFast") { result ->
                    assertThat(result.task(":detektFailFast")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    assertThat(projectFile("build/reports/failfast.xml")).exists()
                    assertThat(projectFile("build/reports/failfast.html")).exists()
                    assertThat(projectFile("build/reports/failfast.txt")).exists()
                }
            }

            it("can be done using the kotlin dsl") {

                val config = """
                    |task<io.gitlab.arturbosch.detekt.Detekt>("detektFailFast") {
                    |    description = "Runs a failfast detekt build."
                    |
                    |    input = files("${"$"}projectDir")
                    |    setIncludes(listOf("**/*.kt", "**/*.kts"))
                    |    setExcludes(listOf("build/"))
                    |    config = files("config.yml")
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

                val gradleRunner = kotlin().withDetektConfig(config).build()
                gradleRunner.writeProjectFile("config.yml", "")
                gradleRunner.runTasksAndCheckResult("detektFailFast") { result ->
                    assertThat(result.task(":detektFailFast")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    assertThat(gradleRunner.projectFile("build/reports/failfast.xml")).exists()
                    assertThat(gradleRunner.projectFile("build/reports/failfast.html")).exists()
                    assertThat(gradleRunner.projectFile("build/reports/failfast.txt")).exists()
                }
            }
        }
    }
})
