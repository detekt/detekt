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
            gradleRunner.runTasks(":shared:detektBaselineMainMetadata")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektMainMetadata") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]mainMetadata.xml""")
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
            gradleRunner.runTasks(":shared:detektBaselineMainJvmBackend")
            gradleRunner.runTasks(":shared:detektBaselineTestJvmBackend")
            gradleRunner.runTasks(":shared:detektBaselineMainJvmEmbedded")
            gradleRunner.runTasks(":shared:detektBaselineTestJvmEmbedded")
        }

        @Test
        fun `configures detekt task with type resolution backend`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektMainJvmBackend") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-main.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]mainJvmBackend.xml""")
                assertDetektWithClasspath(it)
            }
        }

        @Test
        fun `configures detekt task with type resolution embedded`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektMainJvmEmbedded") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-main.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]mainJvmEmbedded.xml""")
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
                                androidTarget {
                                    compilations.all {
                                        kotlinOptions.jvmTarget = "1.8"
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
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]debug.xml""")
                assertDetektWithClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektReleaseAndroid") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-release.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]release.xml""")
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
            gradleRunner.runTasks(":shared:detektBaselineMainJs")
            gradleRunner.runTasks(":shared:detektBaselineTestJs")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektMainJs") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]mainJs.xml""")
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektTestJs") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]testJs.xml""")
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
            gradleRunner.runTasks(":shared:detektBaselineMainIosArm64")
            gradleRunner.runTasks(":shared:detektBaselineTestIosArm64")
            gradleRunner.runTasks(":shared:detektBaselineMainIosX64")
            gradleRunner.runTasks(":shared:detektBaselineTestIosX64")
        }

        @Test
        fun `configures detekt task without type resolution`() {
            gradleRunner.runTasksAndCheckResult(":shared:detektMainIosArm64") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]mainIosArm64.xml""")
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektTestIosArm64") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]testIosArm64.xml""")
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektMainIosX64") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]mainIosX64.xml""")
                assertDetektWithoutClasspath(it)
            }
            gradleRunner.runTasksAndCheckResult(":shared:detektTestIosX64") {
                assertThat(it.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                assertThat(it.output).containsPattern("""--report xml:\S*[/\\]testIosX64.xml""")
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
