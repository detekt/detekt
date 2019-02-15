package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.groovy
import io.gitlab.arturbosch.detekt.DslTestBuilder.Companion.kotlin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

const val SOURCE_DIRECTORY = "src/main/java"

/**
 * @author Markus Schwarz
 */
internal class DetektTaskMultiModuleTest : Spek({
    describe("The Detekt Gradle plugin used in a multi module project") {
        describe("is applied with defaults to all subprojects individually without sources in root project using the" +
                " " +
                "subprojects block") {
            val projectLayout = ProjectLayout(0)
                    .withSubmodule("child1", 2)
                    .withSubmodule("child2", 4)

            lateinit var gradleRunner: DslGradleRunner

            afterEachTest {
                gradleRunner.setupProject()
                gradleRunner.runDetektTaskAndCheckResult { result ->
                    assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.NO_SOURCE)
                    projectLayout.submodules.forEach { submodule ->
                        assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("number of classes: ${submodule.numberOfSourceFiles}")
                    }

                    assertThat(projectFile("build/reports/detekt/detekt.xml")).doesNotExist()
                    assertThat(projectFile("build/reports/detekt/detekt.html")).doesNotExist()
                    projectLayout.submodules.forEach {
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.html")).exists()
                    }
                }
            }
            it("can be done using the groovy dsl") {

                val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.DetektPlugin
				|
				|plugins {
				|   id "java-library"
				|   id "io.gitlab.arturbosch.detekt"
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|}
				|subprojects {
				|	apply plugin: "java-library"
				|	apply plugin: "io.gitlab.arturbosch.detekt"
				|}
				""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
            }
            it("can be done using the kotlin dsl") {

                val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.detekt
				|
				|plugins {
				|   `java-library`
				|	id("io.gitlab.arturbosch.detekt")
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|}
				|subprojects {
				|	plugins.apply("java-library")
				|	plugins.apply("io.gitlab.arturbosch.detekt")
				|}
				""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
            }
        }
        describe("is applied with defaults to main project and subprojects individually using the allprojects block") {
            val projectLayout = ProjectLayout(1)
                    .withSubmodule("child1", 2)
                    .withSubmodule("child2", 4)

            lateinit var gradleRunner: DslGradleRunner

            afterEachTest {
                gradleRunner.setupProject()
                gradleRunner.runDetektTaskAndCheckResult { result ->
                    assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    projectLayout.submodules.forEach { submodule ->
                        assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        assertThat(result.output).contains("number of classes: ${submodule.numberOfSourceFiles}")
                    }

                    assertThat(projectFile("build/reports/detekt/detekt.xml")).exists()
                    assertThat(projectFile("build/reports/detekt/detekt.html")).exists()
                    projectLayout.submodules.forEach {
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("${it.name}/build/reports/detekt/detekt.html")).exists()
                    }
                }
            }
            it("can be done using the groovy dsl") {

                val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.DetektPlugin
				|
				|plugins {
				|   id "java-library"
				|   id "io.gitlab.arturbosch.detekt"
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|	apply plugin: "java-library"
				|	apply plugin: "io.gitlab.arturbosch.detekt"
				|}
				""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
            }
            it("can be done using the kotlin dsl") {

                val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.detekt
				|
				|plugins {
				|   `java-library`
				|	id("io.gitlab.arturbosch.detekt")
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|	plugins.apply("java-library")
				|	plugins.apply("io.gitlab.arturbosch.detekt")
				|}
				""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
            }
        }
        describe("uses custom configs when configured in allprojects block") {
            val projectLayout = ProjectLayout(1)
                    .withSubmodule("child1", 2)
                    .withSubmodule("child2", 4)

            lateinit var gradleRunner: DslGradleRunner

            afterEachTest {
                gradleRunner.setupProject()
                gradleRunner.runDetektTaskAndCheckResult { result ->
                    assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    projectLayout.submodules.forEach { submodule ->
                        assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    }

                    assertThat(projectFile("build/detekt-reports/detekt.xml")).exists()
                    assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
                    projectLayout.submodules.forEach {
                        assertThat(projectFile("${it.name}/build/detekt-reports/detekt.xml")).exists()
                        assertThat(projectFile("${it.name}/build/detekt-reports/detekt.html")).exists()
                    }
                }
            }
            it("can be done using the groovy dsl") {

                val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.DetektPlugin
				|
				|plugins {
				|   id "java-library"
				|   id "io.gitlab.arturbosch.detekt"
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|	apply plugin: "java-library"
				|	apply plugin: "io.gitlab.arturbosch.detekt"
				|	detekt {
				|		reportsDir = file("build/detekt-reports")
				|	}
				|}
				""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
            }
            it("can be done using the kotlin dsl") {

                val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.detekt
				|
				|plugins {
				|   `java-library`
				|	id("io.gitlab.arturbosch.detekt")
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|	plugins.apply("java-library")
				|	plugins.apply("io.gitlab.arturbosch.detekt")
				|	detekt {
				|		reportsDir = file("build/detekt-reports")
				|	}
				|}
				""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
            }
        }
        describe("allows changing defaults in allprojects block that can be overwritten in subprojects") {
            val child2DetektConfig = """
				| detekt {
				| 	reportsDir = file("build/custom")
				| }
			""".trimMargin()
            val projectLayout = ProjectLayout(1)
                    .withSubmodule("child1", 2)
                    .withSubmodule("child2", 4, detektConfig = child2DetektConfig)

            lateinit var gradleRunner: DslGradleRunner

            afterEachTest {
                gradleRunner.setupProject()
                gradleRunner.runDetektTaskAndCheckResult { result ->
                    assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    projectLayout.submodules.forEach { submodule ->
                        assertThat(result.task(":${submodule.name}:detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                    }

                    assertThat(projectFile("build/detekt-reports/detekt.xml")).exists()
                    assertThat(projectFile("build/detekt-reports/detekt.html")).exists()
                    assertThat(projectFile("child1/build/detekt-reports/detekt.xml")).exists()
                    assertThat(projectFile("child1/build/detekt-reports/detekt.html")).exists()
                    assertThat(projectFile("child2/build/custom/detekt.xml")).exists()
                    assertThat(projectFile("child2/build/custom/detekt.html")).exists()
                }
            }
            it("can be done using the groovy dsl") {

                val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.DetektPlugin
				|
				|plugins {
				|   id "java-library"
				|   id "io.gitlab.arturbosch.detekt"
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|	apply plugin: "java-library"
				|	apply plugin: "io.gitlab.arturbosch.detekt"
				|	detekt {
				|		reportsDir = file("build/detekt-reports")
				|	}
				|}
				""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle", mainBuildFileContent)
            }
            it("can be done using the kotlin dsl") {

                val mainBuildFileContent: String = """
				|import io.gitlab.arturbosch.detekt.detekt
				|
				|plugins {
				|   `java-library`
				|	id("io.gitlab.arturbosch.detekt")
				|}
				|
				|allprojects {
				|	repositories {
				|		mavenLocal()
				|		jcenter()
				|	}
				|	plugins.apply("java-library")
				|	plugins.apply("io.gitlab.arturbosch.detekt")
				|	detekt {
				|		reportsDir = file("build/detekt-reports")
				|	}
				|}
				""".trimMargin()

                gradleRunner = DslGradleRunner(projectLayout, "build.gradle.kts", mainBuildFileContent)
            }
        }
        listOf(groovy(), kotlin()).forEach { builder ->
            val projectLayout = ProjectLayout(1)
                    .withSubmodule("child1", 2)
                    .withSubmodule("child2", 4)

            val detektConfig: String = """
				|detekt {
				|	input = files("${"$"}projectDir/src", "${"$"}projectDir/child1/src", "${"$"}projectDir/child2/src")
				|	filters = ".*build.gradle.kts"
				|}
				""".trimMargin()
            val gradleRunner = builder
                    .withProjectLayout(projectLayout)
                    .withDetektConfig(detektConfig)
                    .build()

            describe("can be used in ${builder.gradleBuildName}") {
                it("can be applied to all files in entire project resulting in 1 report") {
                    gradleRunner.runDetektTaskAndCheckResult { result ->
                        assertThat(result.task(":detekt")?.outcome).isEqualTo(TaskOutcome.SUCCESS)
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(result.task(":${submodule.name}:detekt")).isNull()
                        }

                        assertThat(result.output).contains("number of classes: 7")

                        assertThat(projectFile("build/reports/detekt/detekt.xml")).exists()
                        assertThat(projectFile("build/reports/detekt/detekt.html")).exists()
                        projectLayout.submodules.forEach { submodule ->
                            assertThat(projectFile("${submodule.name}/build/reports/detekt/detekt.xml")).doesNotExist()
                            assertThat(projectFile("${submodule.name}/build/reports/detekt/detekt.html")).doesNotExist()
                        }
                    }
                }
            }
        }
    }
})
