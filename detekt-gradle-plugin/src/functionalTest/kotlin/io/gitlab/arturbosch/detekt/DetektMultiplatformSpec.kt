package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import io.gitlab.arturbosch.detekt.testkit.joinGradleBlocks
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildResult
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.condition.EnabledOnOs
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
            gradleRunner.runTasks(":shared:detektBaselineCommonMainSourceSet")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektCommonMainSourceSet") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]commonMainSourceSet.xml""")
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
            gradleRunner.runTasksAndExpectFailure(":shared:detektBaselineMainMetadata") { result ->
                assertThat(result.output).containsIgnoringCase("Task 'detektBaselineMainMetadata' not found in project")
            }
        }

        @Test
        fun `does not configure detekt task`() {
            gradleRunner.runTasksAndExpectFailure(":shared:detektMainMetadata") { result ->
                assertThat(result.output).containsIgnoringCase("Task 'detektMainMetadata' not found in project")
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
                            kotlin {
                                jvm()
                            }
                        """.trimIndent(),
                        DETEKT_BLOCK,
                    ),
                    srcDirs = listOf(
                        "src/commonMain/kotlin",
                        "src/commonTest/kotlin",
                        "src/jvmMain/kotlin",
                    ),
                    baselineFiles = listOf("detekt-baseline.xml", "detekt-baseline-main.xml")
                )
            }

        @Test
        fun `configures baseline task`() {
            gradleRunner.runTasks(":shared:detektBaselineMainJvm")
            gradleRunner.runTasks(":shared:detektBaselineTestJvm")
        }

        @Test
        fun `configures detekt task with type resolution backend`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektMainJvm") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-main.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]mainJvm.xml""")
                assertDetektWithClasspath(it)
            }
        }
    }

    @Nested
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
                                compileSdk = 34
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
                                androidTarget {
                                    compilerOptions {
                                        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
                                    }
                                }
                            }
                        """.trimIndent(),
                        DETEKT_BLOCK,
                    ),
                    srcDirs = listOf(
                        "src/androidDebug/kotlin",
                        "src/androidRelease/kotlin",
                        "src/androidUnitTest/kotlin",
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
            gradleRunner.runTasks(":shared:detektBaselineDebugAndroid")
            gradleRunner.runTasks(":shared:detektBaselineReleaseAndroid")
        }

        @Test
        fun `configures test tasks`() {
            gradleRunner.runTasks(":shared:detektDebugAndroidTestAndroid")
            gradleRunner.runTasks(":shared:detektDebugUnitTestAndroid")
            gradleRunner.runTasks(":shared:detektReleaseUnitTestAndroid")
        }

        @Test
        fun `configures detekt task with type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektDebugAndroid") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debug.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]debugAndroid.xml""")
                assertDetektWithClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektReleaseAndroid") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-release.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]releaseAndroid.xml""")
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
            }.apply { disableIP = true }

        @Test
        fun `configures baseline task`() {
            gradleRunner.runTasks(":shared:detektBaselineJsMainSourceSet")
            gradleRunner.runTasks(":shared:detektBaselineJsTestSourceSet")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektJsMainSourceSet") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]jsMainSourceSet.xml""")
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektJsTestSourceSet") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]jsTestSourceSet.xml""")
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
                                iosArm64()
                                iosX64()
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
            gradleRunner.runTasks(":shared:detektBaselineIosArm64MainSourceSet")
            gradleRunner.runTasks(":shared:detektBaselineIosArm64TestSourceSet")
            gradleRunner.runTasks(":shared:detektBaselineIosX64MainSourceSet")
            gradleRunner.runTasks(":shared:detektBaselineIosX64TestSourceSet")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektIosArm64MainSourceSet") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]iosArm64MainSourceSet.xml""")
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosArm64TestSourceSet") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]iosArm64TestSourceSet.xml""")
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosX64MainSourceSet") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]iosX64MainSourceSet.xml""")
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektIosX64TestSourceSet") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]iosX64TestSourceSet.xml""")
                assertDetektWithoutClasspath(it)
            }
        }
    }
}

private fun setupProject(projectLayoutAction: ProjectLayout.() -> Unit): DslGradleRunner =
    DslGradleRunner(
        projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply { projectLayoutAction() },
        buildFileName = "build.gradle.kts",
        settingsContent = """
            dependencyResolutionManagement {
                repositories {
                    mavenLocal()
                    mavenCentral()
                    google()
                }
            }
        """.trimIndent(),
        dryRun = true
    ).also {
        it.setupProject()
    }

private fun setupAndroidProject(projectLayoutAction: ProjectLayout.() -> Unit): DslGradleRunner {
    val gradleRunner = setupProject { projectLayoutAction() }
    gradleRunner.writeProjectFile("shared/src/androidMain/AndroidManifest.xml", manifestContent)
    gradleRunner.writeProjectFile("shared/src/androidDebug/AndroidManifest.xml", manifestContent)
    gradleRunner.writeProjectFile("shared/src/androidRelease/AndroidManifest.xml", manifestContent)
    return gradleRunner
}

private fun setupMultiplatformProject(projectLayoutAction: ProjectLayout.() -> Unit): DslGradleRunner {
    val gradleRunner = setupProject { projectLayoutAction() }
    gradleRunner.writeProjectFile("gradle.properties", "detekt.multiplatform.disabled=true")
    return gradleRunner
}

private fun assertDetektWithoutClasspath(buildResult: BuildResult) {
    assertThat(buildResult.output).contains("--report sarif:")
    assertThat(buildResult.output).doesNotContain("--report md:")
    assertThat(buildResult.output).doesNotContain("--classpath")
}

private fun assertDetektWithClasspath(buildResult: BuildResult) {
    assertThat(buildResult.output).contains("--report sarif:")
    assertThat(buildResult.output).doesNotContain("--report md:")
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
        reports.md.required.set(false)
    }
""".trimIndent()

fun isXCodeInstalled(): Boolean =
    try {
        val process = ProcessBuilder()
            .command("xcode-select", "--print-path")
            .start()
        val terminates = process.waitFor(50, TimeUnit.MILLISECONDS)
        terminates && process.exitValue() == 0
    } catch (ignored: Throwable) {
        false
    }
