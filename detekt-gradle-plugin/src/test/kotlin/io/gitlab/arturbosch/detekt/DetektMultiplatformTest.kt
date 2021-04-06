package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe

class DetektMultiplatformTest : Spek({

    describe("multiplatform projects - Common target") {

        val gradleRunner = setupProject {
            addSubmodule(
                "shared",
                1,
                1,
                buildFileContent = """
                    $KMM_PLUGIN_BLOCK
                    kotlin {
                        jvm()
                    }
                    $DETEKT_BLOCK
                """.trimIndent(),
                srcDirs = listOf("src/commonMain/kotlin", "src/commonTest/kotlin"),
                baselineFiles = listOf("baseline.xml", "baseline-metadataMain.xml")
            )
        }

        it("configures baseline task") {
            gradleRunner.runTasks(":shared:detektBaselineMetadataMain")
        }

        it("configures detekt task without type resolution") {
            gradleRunner.runTasksAndCheckResult(":shared:detektMetadataMain") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
        }

        it("configures check tasks") {
            gradleRunner.runTasksAndCheckResult(":shared:check") { buildResult ->
                assertThat(buildResult.task(":shared:detektMetadataMain")).isNotNull
            }
        }
    }

    describe("multiplatform projects - detekt plain only if user opts out") {

        val gradleRunner = setupProject {
            addSubmodule(
                "shared",
                1,
                1,
                buildFileContent = """
                    $KMM_PLUGIN_BLOCK
                    kotlin {
                        jvm()
                    }
                    $DETEKT_BLOCK
                """.trimIndent(),
                srcDirs = listOf("src/commonMain/kotlin", "src/commonTest/kotlin")
            )
        }
        gradleRunner.writeProjectFile("gradle.properties", "detekt.multiplatform.disabled=true")

        it("does not configure baseline task") {
            gradleRunner.runTasksAndExpectFailure(":shared:detektBaselineMetadataMain") { result ->
                assertThat(result.output).contains("Task 'detektBaselineMetadataMain' not found in project")
            }
        }

        it("does not configure detekt task") {
            gradleRunner.runTasksAndExpectFailure(":shared:detektMetadataMain") { result ->
                assertThat(result.output).contains("Task 'detektMetadataMain' not found in project")
            }
        }
    }

    describe("multiplatform projects - JVM target") {
        val gradleRunner = setupProject {
            addSubmodule(
                "shared",
                1,
                1,
                buildFileContent = """
                    $KMM_PLUGIN_BLOCK
                    kotlin {
                        jvm("jvmBackend")
                        jvm("jvmEmbedded")
                    }
                    $DETEKT_BLOCK
                """.trimIndent(),
                srcDirs = listOf(
                    "src/commonMain/kotlin",
                    "src/commonTest/kotlin",
                    "src/jvmBackendMain/kotlin",
                    "src/jvmEmbeddedMain/kotlin",
                ),
                baselineFiles = listOf("baseline.xml", "baseline-main.xml")
            )
        }

        it("configures baseline task") {
            gradleRunner.runTasks(":shared:detektBaselineJvmBackendMain")
            gradleRunner.runTasks(":shared:detektBaselineJvmBackendTest")
            gradleRunner.runTasks(":shared:detektBaselineJvmEmbeddedMain")
            gradleRunner.runTasks(":shared:detektBaselineJvmEmbeddedTest")
        }

        it("configures detekt task with type resolution backend") {
            gradleRunner.runTasksAndCheckResult(":shared:detektJvmBackendMain") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline-main.xml """)
                assertDetektWithClasspath(it)
            }
        }

        it("configures detekt task with type resolution embedded") {
            gradleRunner.runTasksAndCheckResult(":shared:detektJvmEmbeddedMain") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline-main.xml """)
                assertDetektWithClasspath(it)
            }
        }

        it("configures check tasks") {
            gradleRunner.runTasksAndCheckResult(":shared:check") { buildResult ->
                assertThat(buildResult.task(":shared:detektJvmBackendMain")).isNotNull
                assertThat(buildResult.task(":shared:detektJvmBackendTest")).isNotNull
                assertThat(buildResult.task(":shared:detektJvmEmbeddedMain")).isNotNull
                assertThat(buildResult.task(":shared:detektJvmEmbeddedMain")).isNotNull
            }
        }
    }

    describe(
        "multiplatform projects - Android target",
        skip = if (isAndroidSdkInstalled()) Skip.No else Skip.Yes("No android sdk.")
    ) {
        val gradleRunner = setupProject {
            addSubmodule(
                "shared",
                1,
                1,
                buildFileContent = """
                    plugins {
                        id "kotlin-multiplatform"
                        id "com.android.library"
                        id "io.gitlab.arturbosch.detekt"
                    }
                    android {
                        compileSdkVersion 30
                        buildTypes {
                            release {
                            }
                            debug {
                            }
                        }
                    }
                    kotlin {
                        android()
                    }
                    $DETEKT_BLOCK
                """.trimIndent(),
                srcDirs = listOf(
                    "src/debug/kotlin",
                    "src/release/kotlin",
                    "src/androidTest/kotlin",
                    "src/androidMain/kotlin",
                    "src/commonMain/kotlin",
                    "src/commonTest/kotlin"
                ),
                baselineFiles = listOf("baseline.xml", "baseline-debug.xml", "baseline-release.xml")
            )
        }

        gradleRunner.writeProjectFile("shared/src/androidMain/AndroidManifest.xml", MANIFEST_CONTENT)
        gradleRunner.writeProjectFile("shared/src/debug/AndroidManifest.xml", MANIFEST_CONTENT)
        gradleRunner.writeProjectFile("shared/src/release/AndroidManifest.xml", MANIFEST_CONTENT)

        it("configures baseline task") {
            gradleRunner.runTasks(":shared:detektBaselineAndroidDebug")
            gradleRunner.runTasks(":shared:detektBaselineAndroidRelease")
        }

        it("configures detekt task with type resolution") {
            gradleRunner.runTasksAndCheckResult(":shared:detektAndroidDebug") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline-debug.xml """)
                assertDetektWithClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektAndroidRelease") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline-release.xml """)
                assertDetektWithClasspath(it)
            }
        }

        it("configures check tasks") {
            // Investigate KMM setup for Android, currently running `check` would result in failure
            // Could not determine the dependencies of task ':shared:detektAndroidDebugAndroidTest'
            gradleRunner.runTasksAndExpectFailure(":shared:check") {}
        }
    }

    describe("multiplatform projects - JS target") {
        val gradleRunner = setupProject {
            addSubmodule(
                "shared",
                1,
                1,
                buildFileContent = """
                    $KMM_PLUGIN_BLOCK
                    kotlin {
                        js {
                            browser()
                        }
                    }
                    $DETEKT_BLOCK
                """.trimIndent(),
                srcDirs = listOf(
                    "src/commonMain/kotlin",
                    "src/commonTest/kotlin",
                    "src/jsMain/kotlin",
                    "src/jsTest/kotlin",
                ),
                baselineFiles = listOf("baseline.xml")
            )
        }

        it("configures baseline task") {
            gradleRunner.runTasks(":shared:detektBaselineJsMain")
            gradleRunner.runTasks(":shared:detektBaselineJsTest")
        }

        it("configures detekt task without type resolution") {
            gradleRunner.runTasksAndCheckResult(":shared:detektJsMain") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektJsTest") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
        }

        it("configures check tasks") {
            gradleRunner.runTasksAndCheckResult(":shared:check") { buildResult ->
                assertThat(buildResult.task(":shared:detektJsMain")).isNotNull
                assertThat(buildResult.task(":shared:detektJsTest")).isNotNull
            }
        }
    }

    describe(
        "multiplatform projects - iOS target",
        skip = if (isMacOs() && isCI()) {
            Skip.No
        } else {
            Skip.Yes("Not on MacOS.")
        }
    ) {
        val gradleRunner = setupProject {
            addSubmodule(
                "shared",
                1,
                1,
                buildFileContent = """
                    $KMM_PLUGIN_BLOCK
                    kotlin {
                        ios()
                    }
                    $DETEKT_BLOCK
                """.trimIndent(),
                srcDirs = listOf(
                    "src/commonMain/kotlin",
                    "src/commonTest/kotlin",
                    "src/iosArm64Main/kotlin",
                    "src/iosArm64Test/kotlin",
                    "src/iosX64Main/kotlin",
                    "src/iosX64Test/kotlin",
                    "src/iosMain/kotlin",
                ),
                baselineFiles = listOf("baseline.xml")
            )
        }

        it("configures baseline task") {
            gradleRunner.runTasks(":shared:detektBaselineIosArm64Main")
            gradleRunner.runTasks(":shared:detektBaselineIosArm64Test")
            gradleRunner.runTasks(":shared:detektBaselineIosX64Main")
            gradleRunner.runTasks(":shared:detektBaselineIosX64Test")
        }

        it("configures detekt task without type resolution") {
            gradleRunner.runTasksAndCheckResult(":shared:detektIosArm64Main") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosArm64Test") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosX64Main") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosX64Test") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
        }

        it("configures check tasks") {
            gradleRunner.runTasksAndCheckResult(":shared:check") { buildResult ->
                assertThat(buildResult.task(":shared:detektIosArm64Main")).isNotNull
                assertThat(buildResult.task(":shared:detektIosArm64Test")).isNotNull
                assertThat(buildResult.task(":shared:detektIosX64Main")).isNotNull
                assertThat(buildResult.task(":shared:detektIosX64Test")).isNotNull
            }
        }
    }
})

private fun setupProject(projectLayoutAction: ProjectLayout.() -> Unit): DslGradleRunner {
    return DslGradleRunner(
        projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply { projectLayoutAction() },
        buildFileName = "build.gradle",
        mainBuildFileContent = """
            subprojects {
                repositories {
                    mavenCentral()
                    google()
                    jcenter()
                    mavenLocal()
                }
            }
        """.trimIndent(),
        dryRun = true
    ).also {
        it.setupProject()
    }
}

private fun assertDetektWithoutClasspath(buildResult: BuildResult) {
    assertThat(buildResult.output).contains("--report sarif:")
    assertThat(buildResult.output).doesNotContain("--report txt:")
    assertThat(buildResult.output).doesNotContain("--classpath")
}

private fun assertDetektWithClasspath(buildResult: BuildResult) {
    assertThat(buildResult.output).contains("--report sarif:")
    assertThat(buildResult.output).doesNotContain("--report txt:")
    assertThat(buildResult.output).contains("--classpath")
}

private val KMM_PLUGIN_BLOCK = """
    plugins {
        id "kotlin-multiplatform"
        id "io.gitlab.arturbosch.detekt"
    }
""".trimIndent()

private val DETEKT_BLOCK = """
    detekt {
        baseline = file("${"$"}projectDir/baseline.xml")
        reports.txt.enabled = false
    }
""".trimIndent()

private fun isMacOs() = System.getProperty("os.name").contains("mac", ignoreCase = true)

private fun isCI() = System.getProperty("CI").toBoolean()
