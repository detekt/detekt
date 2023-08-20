package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import io.gitlab.arturbosch.detekt.testkit.joinGradleBlocks
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.JRE.JAVA_17
import org.junit.jupiter.api.condition.OS.MAC
import java.util.concurrent.TimeUnit

class DetektMultiplatformSpec {

    @Nested
    inner class `multiplatform projects - Common target` {

        val gradleRunner =
            setupProject {
                addSubmodule(
                    "shared",
                    1,
                    1,
                    buildFileContent = joinGradleBlocks(
                        KMM_PLUGIN_BLOCK,
                        """
                            kotlin {
                                jvm()
                            }
                        """.trimIndent(),
                        DETEKT_BLOCK,
                    ),
                    srcDirs = listOf("src/commonMain/kotlin", "src/commonTest/kotlin"),
                    baselineFiles = listOf("detekt-baseline.xml", "detekt-baseline-metadataMain.xml")
                )
            }

        @Test
        fun `configures baseline task`() {
            gradleRunner.runTasks(":shared:detektBaselineMetadataMain")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektMetadataMain") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
        }
    }

    @Nested
    inner class `multiplatform projects - detekt plain only if user opts out` {

        val gradleRunner =
            setupMultiplatformProject {
                addSubmodule(
                    "shared",
                    1,
                    1,
                    buildFileContent = joinGradleBlocks(
                        KMM_PLUGIN_BLOCK,
                        """
                            kotlin {
                                jvm()
                            }
                        """.trimIndent(),
                        DETEKT_BLOCK,
                    ),
                    srcDirs = listOf("src/commonMain/kotlin", "src/commonTest/kotlin")
                )
            }

        @Test
        fun `does not configure baseline task`() {
            gradleRunner.runTasksAndExpectFailure(":shared:detektBaselineMetadataMain") { result ->
                assertThat(result.output).containsIgnoringCase("Task 'detektBaselineMetadataMain' not found in project")
            }
        }

        @Test
        fun `does not configure detekt task`() {
            gradleRunner.runTasksAndExpectFailure(":shared:detektMetadataMain") { result ->
                assertThat(result.output).containsIgnoringCase("Task 'detektMetadataMain' not found in project")
            }
        }
    }

    @Nested
    inner class `multiplatform projects - JVM target` {
        val gradleRunner =
            setupProject {
                addSubmodule(
                    "shared",
                    1,
                    1,
                    buildFileContent = joinGradleBlocks(
                        KMM_PLUGIN_BLOCK,
                        """
                            val targetType = Attribute.of("com.example.target.type", String::class.java)
                            
                            kotlin {
                                jvm("jvmBackend") {
                                    attributes.attribute(targetType, "jvmBackend")
                                }
                                jvm("jvmEmbedded")
                            }
                        """.trimIndent(),
                        DETEKT_BLOCK,
                    ),
                    srcDirs = listOf(
                        "src/commonMain/kotlin",
                        "src/commonTest/kotlin",
                        "src/jvmBackendMain/kotlin",
                        "src/jvmEmbeddedMain/kotlin",
                    ),
                    baselineFiles = listOf("detekt-baseline.xml", "detekt-baseline-main.xml")
                )
            }

        @Test
        fun `configures baseline task`() {
            gradleRunner.runTasks(":shared:detektBaselineJvmBackendMain")
            gradleRunner.runTasks(":shared:detektBaselineJvmBackendTest")
            gradleRunner.runTasks(":shared:detektBaselineJvmEmbeddedMain")
            gradleRunner.runTasks(":shared:detektBaselineJvmEmbeddedTest")
        }

        @Test
        fun `configures detekt task with type resolution backend`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektJvmBackendMain") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-main.xml """)
                assertDetektWithClasspath(it)
            }
        }

        @Test
        fun `configures detekt task with type resolution embedded`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektJvmEmbeddedMain") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-main.xml """)
                assertDetektWithClasspath(it)
            }
        }
    }

    @Nested
    @EnabledForJreRange(min = JAVA_17, disabledReason = "Android Gradle Plugin 8.0+ requires JDK 17 or newer")
    @EnabledIf("io.gitlab.arturbosch.detekt.DetektAndroidSpecKt#isAndroidSdkInstalled")
    inner class `multiplatform projects - Android target` {
        val gradleRunner =
            setupAndroidProject {
                addSubmodule(
                    "shared",
                    1,
                    1,
                    buildFileContent = joinGradleBlocks(
                        """
                            plugins {
                                kotlin("multiplatform")
                                id("com.android.library")
                                id("io.gitlab.arturbosch.detekt")
                            }
                            android {
                                compileSdk = 30
                                namespace = "io.gitlab.arturbosch.detekt.app"
                                sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
                                buildTypes {
                                    release {
                                    }
                                    debug {
                                    }
                                }
                            }
                            kotlin {
                                android {
                                    compilations.all {
                                        kotlinOptions.jvmTarget = "1.8"
                                    }
                                }
                            }
                        """.trimIndent(),
                        DETEKT_BLOCK,
                    ),
                    srcDirs = listOf(
                        "src/debug/kotlin",
                        "src/release/kotlin",
                        "src/androidTest/kotlin",
                        "src/androidMain/kotlin",
                        "src/commonMain/kotlin",
                        "src/commonTest/kotlin"
                    ),
                    baselineFiles = listOf(
                        "detekt-baseline.xml",
                        "detekt-baseline-debug.xml",
                        "detekt-baseline-release.xml"
                    )
                )
            }

        @Test
        fun `configures baseline task`() {
            gradleRunner.runTasks(":shared:detektBaselineAndroidDebug")
            gradleRunner.runTasks(":shared:detektBaselineAndroidRelease")
        }

        @Test
        fun `configures test tasks`() {
            gradleRunner.runTasks(":shared:detektAndroidDebugAndroidTest")
            gradleRunner.runTasks(":shared:detektAndroidDebugUnitTest")
            gradleRunner.runTasks(":shared:detektAndroidReleaseUnitTest")
        }

        @Test
        fun `configures detekt task with type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektAndroidDebug") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debug.xml """)
                assertDetektWithClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektAndroidRelease") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-release.xml """)
                assertDetektWithClasspath(it)
            }
        }
    }

    @Nested
    inner class `multiplatform projects - JS target` {
        val gradleRunner =
            setupProject {
                addSubmodule(
                    "shared",
                    1,
                    1,
                    buildFileContent = joinGradleBlocks(
                        KMM_PLUGIN_BLOCK,
                        """
                            kotlin {
                                js(IR) {
                                    browser()
                                }
                            }
                        """.trimIndent(),
                        DETEKT_BLOCK,
                    ),
                    srcDirs = listOf(
                        "src/commonMain/kotlin",
                        "src/commonTest/kotlin",
                        "src/jsMain/kotlin",
                        "src/jsTest/kotlin",
                    ),
                    baselineFiles = listOf("detekt-baseline.xml")
                )
            }

        @Test
        fun `configures baseline task`() {
            gradleRunner.runTasks(":shared:detektBaselineJsMain")
            gradleRunner.runTasks(":shared:detektBaselineJsTest")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektJsMain") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektJsTest") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
        }
    }

    @Nested
    @EnabledOnOs(MAC)
    @EnabledIf(
        "io.gitlab.arturbosch.detekt.DetektMultiplatformSpecKt#isXCodeInstalled",
        disabledReason = "XCode is not installed."
    )
    inner class `multiplatform projects - iOS target` {
        val gradleRunner =
            setupProject {
                addSubmodule(
                    "shared",
                    1,
                    1,
                    buildFileContent = joinGradleBlocks(
                        KMM_PLUGIN_BLOCK,
                        """
                            kotlin {
                                ios()
                            }
                        """.trimIndent(),
                        DETEKT_BLOCK,
                    ),
                    srcDirs = listOf(
                        "src/commonMain/kotlin",
                        "src/commonTest/kotlin",
                        "src/iosArm64Main/kotlin",
                        "src/iosArm64Test/kotlin",
                        "src/iosX64Main/kotlin",
                        "src/iosX64Test/kotlin",
                        "src/iosMain/kotlin",
                    ),
                    baselineFiles = listOf("detekt-baseline.xml")
                )
            }

        @Test
        fun `configures baseline task`() {
            gradleRunner.runTasks(":shared:detektBaselineIosArm64Main")
            gradleRunner.runTasks(":shared:detektBaselineIosArm64Test")
            gradleRunner.runTasks(":shared:detektBaselineIosX64Main")
            gradleRunner.runTasks(":shared:detektBaselineIosX64Test")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektIosArm64Main") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosArm64Test") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosX64Main") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosX64Test") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertDetektWithoutClasspath(it)
            }
        }
    }
}

private fun setupProject(projectLayoutAction: ProjectLayout.() -> Unit): DslGradleRunner {
    return DslGradleRunner(
        projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply { projectLayoutAction() },
        buildFileName = "build.gradle.kts",
        mainBuildFileContent = """
            subprojects {
                repositories {
                    mavenCentral()
                    google()
                    exclusiveContent {
                        forRepository {
                            ivy {
                                url = uri("${System.getenv("DGP_PROJECT_DEPS_REPO_PATH")}")
                            }
                        }
                        filter {
                            includeGroup("io.gitlab.arturbosch.detekt")
                        }
                    }
                }
            }
        """.trimIndent(),
        dryRun = true
    ).also {
        it.setupProject()
    }
}

private fun setupAndroidProject(projectLayoutAction: ProjectLayout.() -> Unit): DslGradleRunner {
    val gradleRunner = setupProject { projectLayoutAction() }
    gradleRunner.writeProjectFile("shared/src/androidMain/AndroidManifest.xml", manifestContent)
    gradleRunner.writeProjectFile("shared/src/debug/AndroidManifest.xml", manifestContent)
    gradleRunner.writeProjectFile("shared/src/release/AndroidManifest.xml", manifestContent)
    return gradleRunner
}

private fun setupMultiplatformProject(projectLayoutAction: ProjectLayout.() -> Unit): DslGradleRunner {
    val gradleRunner = setupProject { projectLayoutAction() }
    gradleRunner.writeProjectFile("gradle.properties", "detekt.multiplatform.disabled=true")
    return gradleRunner
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

@Language("gradle.kts")
private val KMM_PLUGIN_BLOCK = """
    plugins {
        kotlin("multiplatform")
        id("io.gitlab.arturbosch.detekt")
    }
""".trimIndent()

@Language("gradle.kts")
private val DETEKT_BLOCK = """
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports.txt.required.set(false)
    }
""".trimIndent()

fun isXCodeInstalled(): Boolean {
    return try {
        val process = ProcessBuilder()
            .command("xcode-select", "--print-path")
            .start()
        val terminates = process.waitFor(50, TimeUnit.MILLISECONDS)
        terminates && process.exitValue() == 0
    } catch (ignored: Throwable) {
        false
    }
}
