package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import io.gitlab.arturbosch.detekt.testkit.createJavaClass
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DetektJvmTest : Spek({
    describe("When applying detekt in a JVM project") {

        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 1),
            buildFileName = "build.gradle",
            baselineFiles = listOf("baseline.xml", "baseline-main.xml", "baseline-test.xml"),
            mainBuildFileContent = """
                plugins {
                    id "org.jetbrains.kotlin.jvm"
                    id "io.gitlab.arturbosch.detekt"
                }

                repositories {
                    mavenCentral()
                    jcenter()
                    mavenLocal()
                }

                detekt {
                    reports {
                        txt.enabled = false
                    }
                }
            """.trimIndent(),
            dryRun = true
        )
        gradleRunner.setupProject()
        gradleRunner.createJavaClass("AJavaClass")

        it("configures detekt type resolution task main") {
            gradleRunner.runTasksAndCheckResult(":detektMain") { buildResult ->
                assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]baseline-main.xml """)
                assertThat(buildResult.output).contains("--report xml:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report txt:")
                assertThat(buildResult.output).contains("--classpath")
                assertThat(buildResult.output).doesNotContain("AJavaClass.java")
            }
        }

        it("configures detekt type resolution task test") {
            gradleRunner.runTasksAndCheckResult(":detektTest") { buildResult ->
                assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]baseline-test.xml """)
                assertThat(buildResult.output).contains("--report xml:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report txt:")
                assertThat(buildResult.output).contains("--classpath")
                assertThat(buildResult.output).doesNotContain("AJavaClassTest.java")
            }
        }
    }
})
