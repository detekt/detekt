package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Skip
import org.spekframework.spek2.style.specification.describe

object DetektAndroidTest : Spek({
    describe(
        "When applying detekt in an Android project",
        skip = skipIfAndroidEnvironmentRequirementsUnmet()
    ) {
        describe("configures android tasks for android application") {
            val projectLayout = ProjectLayout(
                numberOfSourceFilesInRootPerSourceDir = 0,
            ).apply {
                addSubmodule(
                    name = "app",
                    numberOfSourceFilesPerSourceDir = 1,
                    numberOfCodeSmells = 1,
                    buildFileContent = """
                        $APP_PLUGIN_BLOCK
                        $ANDROID_BLOCK
                        $DETEKT_BLOCK
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java"),
                    baselineFiles = listOf(
                        "detekt-baseline.xml",
                        "detekt-baseline-release.xml",
                        "detekt-baseline-debug.xml",
                        "detekt-baseline-releaseUnitTest.xml",
                        "detekt-baseline-debugUnitTest.xml",
                        "detekt-baseline-debugAndroidTest.xml"
                    )
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout)
            gradleRunner.writeProjectFile("app/src/main/AndroidManifest.xml", MANIFEST_CONTENT)

            it("task :app:detektMain") {
                gradleRunner.runTasksAndCheckResult(":app:detektMain") { buildResult ->
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-release.xml """)
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debug.xml """)
                    assertThat(buildResult.output).contains("--report xml:")
                    assertThat(buildResult.output).contains("--report sarif:")
                    assertThat(buildResult.output).doesNotContain("--report txt:")
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":app:detektMain",
                            ":app:detektDebug"
                        )
                    )
                }
            }

            it("task :app:detektTest") {
                gradleRunner.runTasksAndCheckResult(":app:detektTest") { buildResult ->
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-releaseUnitTest.xml """)
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debugUnitTest.xml """)
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debugAndroidTest.xml """)
                    assertThat(buildResult.output).contains("--report xml:")
                    assertThat(buildResult.output).contains("--report sarif:")
                    assertThat(buildResult.output).doesNotContain("--report txt:")
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":app:detektDebugUnitTest",
                            ":app:detektDebugAndroidTest"
                        )
                    )
                }
            }

            it("task :app:check") {
                gradleRunner.runTasksAndCheckResult(":app:check") { buildResult ->
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                    assertThat(buildResult.task(":app:detekt")).isNotNull
                    assertThat(buildResult.output).contains("--report xml:")
                    assertThat(buildResult.output).contains("--report sarif:")
                    assertThat(buildResult.output).doesNotContain("--report txt:")
                }
            }
        }

        describe("does not configures android tasks if user opts out") {
            val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
                addSubmodule(
                    name = "app",
                    numberOfSourceFilesPerSourceDir = 1,
                    numberOfCodeSmells = 1,
                    buildFileContent = """
                        $APP_PLUGIN_BLOCK
                        $ANDROID_BLOCK
                        $DETEKT_BLOCK
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout)
            gradleRunner.writeProjectFile("gradle.properties", "detekt.android.disabled=true")
            gradleRunner.writeProjectFile("app/src/main/AndroidManifest.xml", MANIFEST_CONTENT)

            it("task :app:detekt") {
                gradleRunner.runTasks(":app:detekt")
            }

            it("task :app:detektMain") {
                gradleRunner.runTasksAndExpectFailure(":app:detektMain") { result ->
                    assertThat(result.output).contains("Task 'detektMain' not found in project")
                }
            }

            it("task :app:detektTest") {
                gradleRunner.runTasksAndExpectFailure(":app:detektTest") { result ->
                    assertThat(result.output).contains("Task 'detektTest' not found in project")
                }
            }
        }

        describe("configures android tasks for android library") {
            val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
                addSubmodule(
                    name = "lib",
                    numberOfSourceFilesPerSourceDir = 1,
                    numberOfCodeSmells = 1,
                    buildFileContent = """
                        $LIB_PLUGIN_BLOCK
                        $ANDROID_BLOCK
                        $DETEKT_BLOCK
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java"),
                    baselineFiles = listOf(
                        "detekt-baseline.xml",
                        "detekt-baseline-release.xml",
                        "detekt-baseline-debug.xml",
                        "detekt-baseline-releaseUnitTest.xml",
                        "detekt-baseline-debugUnitTest.xml",
                        "detekt-baseline-debugAndroidTest.xml"
                    )
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout)
            gradleRunner.writeProjectFile("lib/src/main/AndroidManifest.xml", MANIFEST_CONTENT)

            it("task :lib:detektMain") {
                gradleRunner.runTasksAndCheckResult(":lib:detektMain") { buildResult ->
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-release.xml """)
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debug.xml """)
                    assertThat(buildResult.output).contains("--report xml:")
                    assertThat(buildResult.output).contains("--report sarif:")
                    assertThat(buildResult.output).doesNotContain("--report txt:")
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektMain",
                            ":lib:detektDebug"
                        )
                    )
                }
            }

            it("task :lib:detektTest") {
                gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-releaseUnitTest.xml """)
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debugUnitTest.xml """)
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debugAndroidTest.xml """)
                    assertThat(buildResult.output).contains("--report xml:")
                    assertThat(buildResult.output).contains("--report sarif:")
                    assertThat(buildResult.output).doesNotContain("--report txt:")
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektDebugUnitTest",
                            ":lib:detektDebugAndroidTest"
                        )
                    )
                }
            }

            it(":lib:check") {
                gradleRunner.runTasksAndCheckResult(":lib:check") { buildResult ->
                    assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline.xml """)
                    assertThat(buildResult.task(":lib:detekt")).isNotNull
                    assertThat(buildResult.output).contains("--report xml:")
                    assertThat(buildResult.output).contains("--report sarif:")
                    assertThat(buildResult.output).doesNotContain("--report txt:")
                }
            }
        }

        describe("configures android tasks for different build variants") {

            val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
                addSubmodule(
                    name = "lib",
                    numberOfSourceFilesPerSourceDir = 1,
                    numberOfCodeSmells = 1,
                    buildFileContent = """
                        $LIB_PLUGIN_BLOCK
                        $ANDROID_BLOCK_WITH_FLAVOR
                        $DETEKT_BLOCK
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout)
            gradleRunner.writeProjectFile("lib/src/main/AndroidManifest.xml", MANIFEST_CONTENT)

            it("task :lib:detektMain") {
                gradleRunner.runTasksAndCheckResult(":lib:detektMain") { buildResult ->
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektYoungHarryDebug",
                            ":lib:detektOldHarryDebug",
                            ":lib:detektOldHarryRelease"
                        )
                    )
                }
            }

            it("task :lib:detektTest") {
                gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektYoungHarryDebugUnitTest",
                            ":lib:detektOldHarryDebugUnitTest",
                            ":lib:detektOldHarryReleaseUnitTest",
                            ":lib:detektYoungHarryDebugAndroidTest",
                            ":lib:detektOldHarryDebugAndroidTest"
                        )
                    )
                }
            }
        }

        describe("configures android tasks for different build variants excluding ignored build types") {

            val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
                addSubmodule(
                    name = "lib",
                    numberOfSourceFilesPerSourceDir = 1,
                    numberOfCodeSmells = 1,
                    buildFileContent = """
                        $LIB_PLUGIN_BLOCK
                        $ANDROID_BLOCK_WITH_FLAVOR
                        detekt {
                            ignoredBuildTypes = ["release"]
                        }
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout)
            gradleRunner.writeProjectFile("lib/src/main/AndroidManifest.xml", MANIFEST_CONTENT)

            it("task :lib:detektMain") {
                gradleRunner.runTasksAndCheckResult(":lib:detektMain") { buildResult ->
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektYoungHarryDebug",
                            ":lib:detektOldHarryDebug"
                        )
                    ).doesNotContain(
                        ":lib:detektOldHarryRelease"
                    )
                }
            }

            it("task :lib:detektTest") {
                gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektYoungHarryDebugUnitTest",
                            ":lib:detektOldHarryDebugUnitTest",
                            ":lib:detektYoungHarryDebugAndroidTest",
                            ":lib:detektOldHarryDebugAndroidTest"
                        )
                    ).doesNotContain(
                        ":lib:detektOldHarryReleaseUnitTest"
                    )
                }
            }
        }

        describe("configures android tasks for different build variants excluding ignored variants") {

            val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
                addSubmodule(
                    name = "lib",
                    numberOfSourceFilesPerSourceDir = 1,
                    numberOfCodeSmells = 1,
                    buildFileContent = """
                        $LIB_PLUGIN_BLOCK
                        $ANDROID_BLOCK_WITH_FLAVOR
                        detekt {
                            ignoredVariants = ["youngHarryDebug", "oldHarryRelease"]
                        }
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout)
            gradleRunner.writeProjectFile("lib/src/main/AndroidManifest.xml", MANIFEST_CONTENT)

            it("task :lib:detektMain") {
                gradleRunner.runTasksAndCheckResult(":lib:detektMain") { buildResult ->
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektOldHarryDebug"
                        )
                    ).doesNotContain(
                        ":lib:detektYoungHarryDebug",
                        ":lib:detektOldHarryRelease"
                    )
                }
            }

            it("task :lib:detektTest") {
                gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektOldHarryDebugUnitTest",
                            ":lib:detektOldHarryDebugAndroidTest"
                        )
                    ).doesNotContain(
                        ":lib:detektYoungHarryDebugUnitTest",
                        ":lib:detektYoungHarryDebugAndroidTest",
                        ":lib:detektOldHarryReleaseUnitTest"
                    )
                }
            }
        }

        describe("configures android tasks for different build variants excluding ignored flavors") {

            val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
                addSubmodule(
                    name = "lib",
                    numberOfSourceFilesPerSourceDir = 1,
                    numberOfCodeSmells = 1,
                    buildFileContent = """
                        $LIB_PLUGIN_BLOCK
                        $ANDROID_BLOCK_WITH_FLAVOR
                        detekt {
                            ignoredFlavors = ["youngHarry"]
                        }
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout)
            gradleRunner.writeProjectFile("lib/src/main/AndroidManifest.xml", MANIFEST_CONTENT)

            it("task :lib:detektMain") {
                gradleRunner.runTasksAndCheckResult(":lib:detektMain") { buildResult ->
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektOldHarryDebug",
                            ":lib:detektOldHarryRelease"
                        )
                    ).doesNotContain(
                        ":lib:detektYoungHarryDebug"
                    )
                }
            }

            it("task :lib:detektTest") {
                gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                    assertThat(buildResult.tasks.map { it.path }).containsAll(
                        listOf(
                            ":lib:detektOldHarryDebugUnitTest",
                            ":lib:detektOldHarryDebugAndroidTest",
                            ":lib:detektOldHarryReleaseUnitTest"
                        )
                    ).doesNotContain(
                        ":lib:detektYoungHarryDebugUnitTest",
                        ":lib:detektYoungHarryDebugAndroidTest"
                    )
                }
            }
        }

        describe("android tasks have javac intermediates on classpath") {
            val projectLayout = ProjectLayout(
                numberOfSourceFilesInRootPerSourceDir = 0,
            ).apply {
                addSubmodule(
                    name = "app",
                    numberOfSourceFilesPerSourceDir = 0,
                    numberOfCodeSmells = 0,
                    buildFileContent = """
                        $APP_PLUGIN_BLOCK
                        $ANDROID_BLOCK_WITH_VIEW_BINDING
                        $DETEKT_BLOCK
                    """.trimIndent(),
                    srcDirs = listOf("src/main/java"),
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout, dryRun = false)

            gradleRunner.projectFile("app/src/main/java").mkdirs()
            gradleRunner.projectFile("app/src/main/res/layout").mkdirs()
            gradleRunner.writeProjectFile("app/src/main/AndroidManifest.xml", MANIFEST_CONTENT)
            gradleRunner.writeProjectFile("app/src/main/res/layout/activity_sample.xml", SAMPLE_ACTIVITY_LAYOUT)
            gradleRunner.writeProjectFile("app/src/main/java/SampleActivity.kt", SAMPLE_ACTIVITY_USING_VIEW_BINDING)

            it("task :app:detektMain has javac intermediates on the classpath") {
                gradleRunner.runTasksAndCheckResult(":app:detektMain") { buildResult ->
                    assertThat(buildResult.output).doesNotContain("error: unresolved reference: databinding")
                }
            }

            it("task :app:detektTest has javac intermediates on the classpath") {
                gradleRunner.runTasksAndCheckResult(":app:detektTest") { buildResult ->
                    assertThat(buildResult.output).doesNotContain("error: unresolved reference: databinding")
                }
            }
        }
    }
})

/**
 * ANDROID_SDK_ROOT is preferred over ANDROID_HOME, but the check here is more lenient.
 * See [Android CLI Environment Variables](https://developer.android.com/studio/command-line/variables.html)
 */
internal fun isAndroidSdkInstalled() =
    System.getenv("ANDROID_SDK_ROOT") != null || System.getenv("ANDROID_HOME") != null

internal fun skipIfAndroidEnvironmentRequirementsUnmet() = when {
    !isAndroidSdkInstalled() -> Skip.Yes("No android SDK.")
    getJdkVersion() >= 16 -> Skip.Yes("Android 4.1.3 & 4.2.1 don't run on JDK 16 or higher")
    else -> Skip.No
}

internal val MANIFEST_CONTENT = """
    <manifest package="io.gitlab.arturbosch.detekt.app"
        xmlns:android="http://schemas.android.com/apk/res/android"/>
""".trimIndent()

private val APP_PLUGIN_BLOCK = """
    plugins {
        id "com.android.application"
        id "kotlin-android"
        id "io.gitlab.arturbosch.detekt"
    }
""".trimIndent()

private val LIB_PLUGIN_BLOCK = """
    plugins {
        id "com.android.library"
        id "kotlin-android"
        id "io.gitlab.arturbosch.detekt"
    }
""".trimIndent()

private val ANDROID_BLOCK = """
    android {
       compileSdkVersion 30
    }
""".trimIndent()

private val ANDROID_BLOCK_WITH_FLAVOR = """
    android {
        compileSdkVersion 30
        flavorDimensions("age", "name")
        productFlavors {
           harry {
             dimension = "name"
           }
           young {
             dimension = "age"
           }
           old {
             dimension = "age"
           }
        }
    }
""".trimIndent()

private val ANDROID_BLOCK_WITH_VIEW_BINDING = """
    android {
        compileSdkVersion = 30
        defaultConfig {
            applicationId = "io.gitlab.arturbosch.detekt.app"
            minSdkVersion = 24
        }
        buildFeatures {
            viewBinding = true
        }
    }
""".trimIndent()

private val DETEKT_BLOCK = """
    detekt {
        reports {
            txt.enabled = false
        }
    }
""".trimIndent()

private val SAMPLE_ACTIVITY_LAYOUT = """
    <?xml version="1.0" encoding="utf-8"?>
    <View
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/sample_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
""".trimIndent()

private val SAMPLE_ACTIVITY_USING_VIEW_BINDING = """
    package io.gitlab.arturbosch.detekt.app
    
    import android.app.Activity
    import android.os.Bundle
    import android.view.LayoutInflater
    import io.gitlab.arturbosch.detekt.app.databinding.ActivitySampleBinding
    
    class SampleActivity : Activity() {
    
        private lateinit var binding: ActivitySampleBinding
    
        override fun onCreate(savedInstanceState: Bundle?) {
            binding = ActivitySampleBinding.inflate(LayoutInflater.from(this))
            setContentView(binding.root)
        }
    }
""".trimIndent() + "\n" // new line at end of file rule

private fun createGradleRunnerAndSetupProject(
    projectLayout: ProjectLayout,
    dryRun: Boolean = true,
) = DslGradleRunner(
    projectLayout = projectLayout,
    buildFileName = "build.gradle",
    mainBuildFileContent = """
        subprojects {
            repositories {
                mavenCentral()
                google()
                mavenLocal()
            }
        }
    """.trimIndent(),
    dryRun = dryRun,
).also { it.setupProject() }
