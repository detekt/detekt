package dev.detekt.gradle.plugin

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.JRE
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DetektBasePluginSpec {
    @Test
    fun `generates source set tasks for JVM project`() {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(
                numberOfSourceFilesInRootPerSourceDir = 1,
                srcDirs = listOf(
                    "src/main/kotlin",
                    "src/test/kotlin",
                ),
            ),
            buildFileName = "build.gradle.kts",
            mainBuildFileContent = """
                plugins {
                    id("io.gitlab.arturbosch.detekt")
                    kotlin("jvm")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral()
                }
            """.trimIndent(),
            dryRun = true,
        ).also {
            it.setupProject()
        }

        gradleRunner.checkTask("main")
        gradleRunner.checkTask("test")
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_17, disabledReason = "Android Gradle Plugin 8.0+ requires JDK 17 or newer")
    fun `generates source set tasks for Android project`() {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(
                numberOfSourceFilesInRootPerSourceDir = 1,
                srcDirs = listOf(
                    "src/main/kotlin",
                    "src/debug/kotlin",
                    "src/test/kotlin",
                    "src/androidTest/kotlin",
                ),
            ),
            buildFileName = "build.gradle.kts",
            mainBuildFileContent = """
                plugins {
                    id("io.gitlab.arturbosch.detekt")
                    id("com.android.library")
                    kotlin("android")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral()
                    google()
                }
                
                android {
                    compileSdk = 34
                    namespace = "dev.detekt.gradle.plugin.app"
                }
            """.trimIndent(),
            dryRun = true,
        ).also {
            it.setupProject()
        }

        gradleRunner.checkTask("main")
        gradleRunner.checkTask("debug")
        gradleRunner.checkTask("test")
        gradleRunner.checkTask("androidTest")
    }

    @Nested
    @EnabledForJreRange(min = JRE.JAVA_17, disabledReason = "Android Gradle Plugin 8.0+ requires JDK 17 or newer")
    inner class `generates source set tasks for KMP project` {
        val gradleRunner = DslGradleRunner(
            projectLayout = ProjectLayout(
                numberOfSourceFilesInRootPerSourceDir = 1,
                srcDirs = listOf(
                    "src/commonMain/kotlin",
                    "src/commonTest/kotlin",
                    "src/androidMain/kotlin",
                    "src/androidUnitTest/kotlin",
                    "src/androidInstrumentedTest/kotlin",
                    "src/jvmMain/kotlin",
                    "src/jvmTest/kotlin",
                    "src/jsMain/kotlin",
                    "src/jsTest/kotlin",
                    "src/iosMain/kotlin",
                    "src/iosTest/kotlin",
                    "src/appleMain/kotlin",
                    "src/appleTest/kotlin",
                    "src/nativeMain/kotlin",
                    "src/nativeTest/kotlin",
                ),
            ),
            buildFileName = "build.gradle.kts",
            mainBuildFileContent = """
                plugins {
                    id("io.gitlab.arturbosch.detekt")
                    kotlin("multiplatform")
                    id("com.android.library")
                }
                
                repositories {
                    mavenLocal()
                    mavenCentral()
                    google()
                }
                
                kotlin {
                    androidTarget()
                    iosArm64()
                    iosSimulatorArm64()
                    jvm()
                    js {
                        browser()
                        nodejs()
                    }
                }
                
                android {
                    compileSdk = 34
                    namespace = "dev.detekt.gradle.plugin.app"
                }
            """.trimIndent(),
            dryRun = true,
        ).also {
            it.setupProject()
        }

        @ParameterizedTest
        @ValueSource(strings = ["commonMain", "commonTest"])
        fun `generates source set tasks for common code`(sourceSetTaskName: String) {
            gradleRunner.checkTask(sourceSetTaskName)
        }

        @ParameterizedTest
        @ValueSource(strings = ["androidMain", "androidUnitTest", "androidInstrumentedTest"])
        fun `generates source set tasks for Android`(sourceSetTaskName: String) {
            gradleRunner.checkTask(sourceSetTaskName)
        }

        @ParameterizedTest
        @ValueSource(strings = ["jvmMain", "jvmTest"])
        fun `generates source set tasks for JVM`(sourceSetTaskName: String) {
            gradleRunner.checkTask(sourceSetTaskName)
        }

        @ParameterizedTest
        @ValueSource(strings = ["jsMain", "jsTest"])
        fun `generates source set tasks for JS`(sourceSetTaskName: String) {
            gradleRunner.checkTask(sourceSetTaskName)
        }

        @ParameterizedTest
        @ValueSource(strings = ["iosMain", "iosTest", "appleMain", "appleTest", "nativeMain", "nativeTest"])
        fun `generates source set tasks for iOS native`(sourceSetTaskName: String) {
            gradleRunner.checkTask(sourceSetTaskName)
        }
    }

    private fun DslGradleRunner.checkTask(sourceSetTaskName: String) {
        runTasksAndCheckResult(":detekt${sourceSetTaskName}SourceSet") { buildResult ->
            assertThat(buildResult.output)
                .containsPattern("""--input \S*[/\\]src[/\\]$sourceSetTaskName[/\\]kotlin""")
            val xmlReportFile = projectFile("build/reports/detekt/${sourceSetTaskName}SourceSet.xml")
            val sarifReportFile = projectFile("build/reports/detekt/${sourceSetTaskName}SourceSet.sarif")
            assertThat(buildResult.output).contains("--report xml:$xmlReportFile")
            assertThat(buildResult.output).contains("--report sarif:$sarifReportFile")
        }
    }
}
