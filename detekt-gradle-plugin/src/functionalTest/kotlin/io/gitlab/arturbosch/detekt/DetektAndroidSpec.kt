package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.testkit.DslGradleRunner
import io.gitlab.arturbosch.detekt.testkit.ProjectLayout
import io.gitlab.arturbosch.detekt.testkit.joinGradleBlocks
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.api.condition.JRE

@EnabledForJreRange(min = JRE.JAVA_17, disabledReason = "Android Gradle Plugin 8.0+ requires JDK 17 or newer")
@EnabledIf("io.gitlab.arturbosch.detekt.DetektAndroidSpecKt#isAndroidSdkInstalled")
class DetektAndroidSpec {

    @Nested
    inner class `configures android tasks for android application` {
        val projectLayout = ProjectLayout(
            numberOfSourceFilesInRootPerSourceDir = 0,
        ).apply {
            addSubmodule(
                name = "app",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    APP_PLUGIN_BLOCK,
                    ANDROID_BLOCK,
                    DETEKT_REPORTS_BLOCK,
                ),
                srcDirs = listOf(
                    "src/main/java",
                    "src/debug/java",
                    "src/test/java",
                    "src/androidTest/java",
                    "src/main/kotlin",
                    "src/debug/kotlin",
                    "src/test/kotlin",
                    "src/androidTest/kotlin",
                ),
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
        val gradleRunner = createGradleRunnerAndSetupProject(projectLayout).also {
            it.writeProjectFile("app/src/main/AndroidManifest.xml", manifestContent)
        }

        @Test
        @DisplayName("task :app:detektMain")
        fun appDetektMain() {
            gradleRunner.runTasksAndCheckResult(":app:detektMain") { buildResult ->
                assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-release.xml """)
                assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debug.xml """)
                assertThat(buildResult.output).containsPattern("""--input \S*[/\\]app[/\\]src[/\\]main[/\\]java""")
                assertThat(buildResult.output).containsPattern("""--input \S*[/\\]app[/\\]src[/\\]debug[/\\]java""")
                assertThat(buildResult.output).containsPattern("""--input \S*[/\\]app[/\\]src[/\\]main[/\\]kotlin""")
                assertThat(buildResult.output).containsPattern("""--input \S*[/\\]app[/\\]src[/\\]debug[/\\]kotlin""")
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

        @Test
        @DisplayName("task :app:detektTest")
        fun appDetektTest() {
            gradleRunner.runTasksAndCheckResult(":app:detektTest") { buildResult ->
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-releaseUnitTest.xml """
                )
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-debugUnitTest.xml """
                )
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-debugAndroidTest.xml """
                )
                assertThat(buildResult.output).containsPattern("""--input \S*[/\\]app[/\\]src[/\\]test[/\\]java""")
                assertThat(buildResult.output).containsPattern(
                    """--input \S*[/\\]app[/\\]src[/\\]androidTest[/\\]java"""
                )
                assertThat(buildResult.output).containsPattern("""--input \S*[/\\]app[/\\]src[/\\]test[/\\]kotlin""")
                assertThat(buildResult.output).containsPattern(
                    """--input \S*[/\\]app[/\\]src[/\\]androidTest[/\\]kotlin"""
                )
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
    }

    @Nested
    inner class `does not configures android tasks if user opts out` {
        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "app",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    APP_PLUGIN_BLOCK,
                    ANDROID_BLOCK,
                    DETEKT_REPORTS_BLOCK,
                ),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
            )
        }
        val gradleRunner = createGradleRunnerAndSetupProject(projectLayout).also {
            it.writeProjectFile("gradle.properties", "detekt.android.disabled=true")
            it.writeProjectFile("app/src/main/AndroidManifest.xml", manifestContent)
        }

        @Test
        @DisplayName("task :app:detekt")
        fun appDetekt() {
            gradleRunner.runTasks(":app:detekt")
        }

        @Test
        @DisplayName("task :app:detektMain")
        fun appDetektMain() {
            gradleRunner.runTasksAndExpectFailure(":app:detektMain") { result ->
                assertThat(result.output).containsIgnoringCase("Task 'detektMain' not found in project")
            }
        }

        @Test
        @DisplayName("task :app:detektTest")
        fun appDetektTest() {
            gradleRunner.runTasksAndExpectFailure(":app:detektTest") { result ->
                assertThat(result.output).containsIgnoringCase("Task 'detektTest' not found in project")
            }
        }
    }

    @Nested
    inner class `configures android tasks for android library` {
        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    LIB_PLUGIN_BLOCK,
                    ANDROID_BLOCK,
                    DETEKT_REPORTS_BLOCK,
                ),
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
        val gradleRunner = createGradleRunnerAndSetupProject(projectLayout).also {
            it.writeProjectFile("lib/src/main/AndroidManifest.xml", manifestContent)
        }

        @Test
        @DisplayName("task :lib:detektMain")
        fun libDetektMain() {
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

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
            gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-releaseUnitTest.xml """
                )
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-debugUnitTest.xml """
                )
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-debugAndroidTest.xml """
                )
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
    }

    @Nested
    inner class `android library depends on kotlin only library with configuration cache turned on` {
        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "kotlin_only_lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    KOTLIN_ONLY_LIB_PLUGIN_BLOCK,
                    DETEKT_REPORTS_BLOCK,
                ),
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
            addSubmodule(
                name = "android_lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    LIB_PLUGIN_BLOCK,
                    ANDROID_BLOCK,
                    DETEKT_REPORTS_BLOCK,
                    """
                        dependencies {
                            implementation(project(":kotlin_only_lib"))
                        }
                    """.trimIndent()
                ),
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
        val gradleRunner = createGradleRunnerAndSetupProject(projectLayout).also {
            it.writeProjectFile("android_lib/src/main/AndroidManifest.xml", manifestContent)
        }

        @Test
        @DisplayName("task :android_lib:detektMain")
        fun libDetektMain() {
            gradleRunner.runTasksAndCheckResult(
                "--configuration-cache",
                ":android_lib:detektMain",
            ) { buildResult ->
                assertThat(buildResult.output).contains("Configuration cache")
                assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-release.xml """)
                assertThat(buildResult.output).containsPattern("""--baseline \S*[/\\]detekt-baseline-debug.xml """)
                assertThat(buildResult.output).contains("--report xml:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report txt:")
                assertThat(buildResult.tasks.map { it.path }).containsAll(
                    listOf(
                        ":android_lib:detektMain",
                    )
                )
            }
        }

        @Test
        @DisplayName("task :android_lib:detektTest")
        fun libDetektTest() {
            gradleRunner.runTasksAndCheckResult(
                "--configuration-cache",
                ":android_lib:detektTest",
            ) { buildResult ->
                assertThat(buildResult.output).contains("Configuration cache")
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-debugUnitTest.xml """
                )
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-debugAndroidTest.xml """
                )
                assertThat(buildResult.output).contains("--report xml:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report txt:")
                assertThat(buildResult.tasks.map { it.path }).containsAll(
                    listOf(
                        ":android_lib:detektTest",
                    )
                )
            }
        }
    }

    @Nested
    inner class `configures android tasks for different build variants` {

        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    LIB_PLUGIN_BLOCK,
                    ANDROID_BLOCK_WITH_FLAVOR,
                    DETEKT_REPORTS_BLOCK,
                ),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
            )
        }
        val gradleRunner = createGradleRunnerAndSetupProject(projectLayout).also {
            it.writeProjectFile("lib/src/main/AndroidManifest.xml", manifestContent)
        }

        @Test
        @DisplayName("task :lib:detektMain")
        fun libDetektMain() {
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

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
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

    @Nested
    inner class `configures android tasks for different build variants excluding ignored build types` {

        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    LIB_PLUGIN_BLOCK,
                    ANDROID_BLOCK_WITH_FLAVOR,
                    """
                        detekt {
                            ignoredBuildTypes = listOf("release")
                        }
                    """.trimIndent(),
                ),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
            )
        }
        val gradleRunner = createGradleRunnerAndSetupProject(projectLayout).also {
            it.writeProjectFile("lib/src/main/AndroidManifest.xml", manifestContent)
        }

        @Test
        @DisplayName("task :lib:detektMain")
        fun libDetektMain() {
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

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
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

    @Nested
    inner class `configures android tasks for different build variants excluding ignored variants` {

        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    LIB_PLUGIN_BLOCK,
                    ANDROID_BLOCK_WITH_FLAVOR,
                    """
                        detekt {
                            ignoredVariants = listOf("youngHarryDebug", "oldHarryRelease")
                        }
                    """.trimIndent(),
                ),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
            )
        }
        val gradleRunner = createGradleRunnerAndSetupProject(projectLayout).also {
            it.writeProjectFile("lib/src/main/AndroidManifest.xml", manifestContent)
        }

        @Test
        @DisplayName("task :lib:detektMain")
        fun libDetektMain() {
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

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
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

    @Nested
    inner class `configures android tasks for different build variants excluding ignored flavors` {

        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfCodeSmells = 1,
                buildFileContent = joinGradleBlocks(
                    LIB_PLUGIN_BLOCK,
                    ANDROID_BLOCK_WITH_FLAVOR,
                    """
                        detekt {
                            ignoredFlavors = listOf("youngHarry")
                        }
                    """.trimIndent(),
                ),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java")
            )
        }
        val gradleRunner = createGradleRunnerAndSetupProject(projectLayout).also {
            it.writeProjectFile("lib/src/main/AndroidManifest.xml", manifestContent)
        }

        @Test
        @DisplayName("task :lib:detektMain")
        fun libDetektMain() {
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

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
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

        @Nested
        inner class `configures android tasks android tasks have javac intermediates on classpath` {
            val projectLayout = ProjectLayout(
                numberOfSourceFilesInRootPerSourceDir = 0,
            ).apply {
                addSubmodule(
                    name = "app",
                    numberOfSourceFilesPerSourceDir = 0,
                    numberOfCodeSmells = 0,
                    buildFileContent = joinGradleBlocks(
                        APP_PLUGIN_BLOCK,
                        ANDROID_BLOCK_WITH_VIEW_BINDING,
                    ),
                    srcDirs = listOf("src/main/java"),
                )
            }
            val gradleRunner = createGradleRunnerAndSetupProject(projectLayout, dryRun = false).also {
                it.projectFile("app/src/main/java").mkdirs()
                it.projectFile("app/src/main/res/layout").mkdirs()
                it.writeProjectFile("app/src/main/AndroidManifest.xml", manifestContent)
                it.writeProjectFile("app/src/main/res/layout/activity_sample.xml", SAMPLE_ACTIVITY_LAYOUT)
                it.writeProjectFile("app/src/main/java/SampleActivity.kt", SAMPLE_ACTIVITY_USING_VIEW_BINDING)
            }

            @Test
            @DisplayName("task :app:detektMain has javac intermediates on the classpath")
            fun libDetektMain() {
                gradleRunner.runTasksAndCheckResult(":app:detektMain") { buildResult ->
                    assertThat(buildResult.output).doesNotContain("UnreachableCode")
                }
            }

            @Test
            @DisplayName("task :app:detektTest has javac intermediates on the classpath")
            fun libDetektTest() {
                gradleRunner.runTasksAndCheckResult(":app:detektTest") { buildResult ->
                    assertThat(buildResult.output).doesNotContain("UnreachableCode")
                }
            }
        }
    }
}

/**
 * ANDROID_SDK_ROOT is preferred over ANDROID_HOME, but the check here is more lenient.
 * See [Android CLI Environment Variables](https://developer.android.com/studio/command-line/variables.html)
 */
internal fun isAndroidSdkInstalled() =
    System.getenv("ANDROID_SDK_ROOT") != null || System.getenv("ANDROID_HOME") != null

@Language("xml")
internal val manifestContent = """
    <!--suppress XmlUnusedNamespaceDeclaration -->
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"/>
""".trimIndent()

@Language("gradle.kts")
private val APP_PLUGIN_BLOCK = """
    plugins {
        id("com.android.application")
        kotlin("android")
        id("io.gitlab.arturbosch.detekt")
    }
""".trimIndent()

@Language("gradle.kts")
private val LIB_PLUGIN_BLOCK = """
    plugins {
        id("com.android.library")
        kotlin("android")
        id("io.gitlab.arturbosch.detekt")
    }
""".trimIndent()

@Language("gradle.kts")
private val KOTLIN_ONLY_LIB_PLUGIN_BLOCK = """
    plugins {
        kotlin("jvm")
        id("io.gitlab.arturbosch.detekt")
    }
""".trimIndent()

@Language("gradle.kts")
private val ANDROID_BLOCK = """
    android {
        compileSdk = 30
        namespace = "io.gitlab.arturbosch.detekt.app"
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
""".trimIndent()

@Language("gradle.kts")
private val ANDROID_BLOCK_WITH_FLAVOR = """
    android {
        compileSdk = 30
        namespace = "io.gitlab.arturbosch.detekt.app"
        flavorDimensions("age", "name")
        productFlavors {
            create("harry") {
                dimension = "name"
            }
            create("young") {
                dimension = "age"
            }
            create("old") {
                dimension = "age"
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
""".trimIndent()

@Language("gradle.kts")
private val ANDROID_BLOCK_WITH_VIEW_BINDING = """
    android {
        compileSdk = 30
        namespace = "io.gitlab.arturbosch.detekt.app"
        defaultConfig {
            applicationId = "io.gitlab.arturbosch.detekt.app"
            minSdk = 24
        }
        buildFeatures {
            viewBinding = true
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
""".trimIndent()

@Language("gradle.kts")
private val DETEKT_REPORTS_BLOCK = """
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            txt.required.set(false)
        }
    }
""".trimIndent()

@Language("xml")
private val SAMPLE_ACTIVITY_LAYOUT = """
    <?xml version="1.0" encoding="utf-8"?>
    <View
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/sample_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
""".trimIndent()

@Language("kotlin")
private val SAMPLE_ACTIVITY_USING_VIEW_BINDING = """
    import android.app.Activity
    import android.os.Bundle
    import android.view.LayoutInflater
    import io.gitlab.arturbosch.detekt.app.databinding.ActivitySampleBinding
    
    class SampleActivity : Activity() {
    
        private lateinit var binding: ActivitySampleBinding
    
        override fun onCreate(savedInstanceState: Bundle?) {
            binding = ActivitySampleBinding.inflate(LayoutInflater.from(this))
            binding.sampleView ?: return
            setContentView(binding.root)
        }
    }
    
""".trimIndent() // Last line to prevent NewLineAtEndOfFile.

private fun createGradleRunnerAndSetupProject(
    projectLayout: ProjectLayout,
    dryRun: Boolean = true,
) = DslGradleRunner(
    projectLayout = projectLayout,
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
    dryRun = dryRun,
).also { it.setupProject() }
