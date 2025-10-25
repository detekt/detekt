package dev.detekt.gradle

import dev.detekt.gradle.testkit.DslGradleRunner
import dev.detekt.gradle.testkit.ProjectLayout
import dev.detekt.gradle.testkit.joinGradleBlocks
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import org.junit.jupiter.params.Parameter
import org.junit.jupiter.params.ParameterizedClass
import org.junit.jupiter.params.provider.CsvSource

@EnabledIf("dev.detekt.gradle.DetektAndroidSpecKt#isAndroidSdkInstalled")
@ParameterizedClass
@CsvSource(
    // android.builtInKotlin, useBuiltInKotlinPlugin
    "false, false", // Opt out of AGP built in Kotlin https://developer.android.com/build/migrate-to-built-in-kotlin#opt-out-of-built-in-kotlin
    "true, false", // Default in AGP 9 - enable AGP built in Kotlin https://developer.android.com/build/migrate-to-built-in-kotlin#enable-built-in-kotlin
    "false, true", // Enable AGP built in Kotlin with module-by-module migration plugin https://developer.android.com/build/migrate-to-built-in-kotlin#module-by-module-migration
    "true, true", // Enable AGP built in Kotlin using both plugin and project-wide property
)
class DetektAndroidSpec {

    @Parameter(0)
    var builtInKotlinPropertyEnabled: String = "no-op"

    @Parameter(1)
    var applyBuiltInKotlinPlugin: String = "no-op"

    val agpBuiltInKotlinUsed
        get() = builtInKotlinPropertyEnabled.toBooleanStrict() || applyBuiltInKotlinPlugin.toBooleanStrict()

    @Nested
    inner class `configures android tasks for android application` {
        val projectLayout = ProjectLayout(
            numberOfSourceFilesInRootPerSourceDir = 0,
        ).apply {
            addSubmodule(
                name = "app",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfFindings = 1,
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
                assertThat(buildResult.output).contains("--report checkstyle:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report md:")
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":app:detekt") }
                    .containsExactlyInAnyOrder(
                        ":app:detektDebug",
                        ":app:detektMain",
                        ":app:detektRelease",
                    )
            }
        }

        @Test
        @DisplayName("task :app:detektTest")
        fun appDetektTest() {
            gradleRunner.runTasksAndCheckResult(":app:detektTest") { buildResult ->
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
                assertThat(buildResult.output).contains("--report checkstyle:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report md:")
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":app:detekt") }
                    .containsExactlyInAnyOrder(
                        ":app:detektDebugAndroidTest",
                        ":app:detektDebugUnitTest",
                        ":app:detektTest",
                    )
            }
        }
    }

    @Nested
    inner class `does not configure Android tasks if user opts out` {
        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "app",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfFindings = 1,
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
        @DisplayName("Task :app:detektMain was not registered")
        fun appDetektMain() {
            gradleRunner.runTasksAndCheckResult(":app:detektMain") { result ->
                assertThat(result.output)
                    .contains("Abbreviated task name 'detektMain' matched 'detektMainSourceSet'")
            }
        }

        @Test
        @DisplayName("Task :app:detektTest was not registered")
        fun appDetektTest() {
            gradleRunner.runTasksAndExpectFailure(":app:detektTest") { result ->
                assertThat(result.output)
                    .contains("Cannot locate tasks that match ':app:detektTest' as task 'detektTest' is ambiguous")
            }
        }
    }

    @Nested
    inner class `configures android tasks for android library` {
        val projectLayout = ProjectLayout(numberOfSourceFilesInRootPerSourceDir = 0).apply {
            addSubmodule(
                name = "lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfFindings = 1,
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
                assertThat(buildResult.output).contains("--report checkstyle:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report md:")
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektDebug",
                        ":lib:detektMain",
                        ":lib:detektRelease",
                    )
            }
        }

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
            gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-debugUnitTest.xml """
                )
                assertThat(buildResult.output).containsPattern(
                    """--baseline \S*[/\\]detekt-baseline-debugAndroidTest.xml """
                )
                assertThat(buildResult.output).contains("--report checkstyle:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report md:")
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektDebugAndroidTest",
                        ":lib:detektDebugUnitTest",
                        ":lib:detektTest",
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
                numberOfFindings = 1,
                buildFileContent = joinGradleBlocks(
                    """
                    plugins {
                        kotlin("jvm")
                        id("dev.detekt")
                    }

                    java {
                        sourceCompatibility = JavaVersion.VERSION_11
                        targetCompatibility = JavaVersion.VERSION_11
                    }

                    kotlin {
                        compilerOptions {
                            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
                        }
                    }

                    """.trimIndent(),
                    DETEKT_REPORTS_BLOCK,
                ),
                srcDirs = listOf("src/main/java", "src/debug/java", "src/test/java", "src/androidTest/java"),
                baselineFiles = listOf(
                    "detekt-baseline.xml",
                    "detekt-baseline-release.xml",
                    "detekt-baseline-debug.xml",
                    "detekt-baseline-debugUnitTest.xml",
                    "detekt-baseline-debugAndroidTest.xml"
                )
            )
            addSubmodule(
                name = "android_lib",
                numberOfSourceFilesPerSourceDir = 1,
                numberOfFindings = 1,
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
                assertThat(buildResult.output).contains("--report checkstyle:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report md:")
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":android_lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":android_lib:detektDebug",
                        ":android_lib:detektMain",
                        ":android_lib:detektRelease",
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
                assertThat(buildResult.output).contains("--report checkstyle:")
                assertThat(buildResult.output).contains("--report sarif:")
                assertThat(buildResult.output).doesNotContain("--report md:")
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":android_lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":android_lib:detektDebugAndroidTest",
                        ":android_lib:detektDebugUnitTest",
                        ":android_lib:detektTest",
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
                numberOfFindings = 1,
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
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektMain",
                        ":lib:detektOldHarryDebug",
                        ":lib:detektOldHarryRelease",
                        ":lib:detektYoungHarryDebug",
                        ":lib:detektYoungHarryRelease",
                    )
            }
        }

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
            gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektOldHarryDebugAndroidTest",
                        ":lib:detektOldHarryDebugUnitTest",
                        ":lib:detektTest",
                        ":lib:detektYoungHarryDebugAndroidTest",
                        ":lib:detektYoungHarryDebugUnitTest",
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
                numberOfFindings = 1,
                buildFileContent = joinGradleBlocks(
                    LIB_PLUGIN_BLOCK,
                    ANDROID_BLOCK_WITH_FLAVOR,
                    """
                        detekt {
                            ignoredBuildTypes = listOf("debug")
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
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektMain",
                        ":lib:detektOldHarryRelease",
                        ":lib:detektYoungHarryRelease",
                    )
                    .doesNotContain(
                        ":lib:detektOldHarryDebug",
                        ":lib:detektYoungHarryDebug",
                    )
            }
        }

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
            gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactly(":lib:detektTest")
                    .doesNotContain(
                        ":lib:detektOldHarryDebugAndroidTest",
                        ":lib:detektOldHarryDebugUnitTest",
                        ":lib:detektYoungHarryDebugAndroidTest",
                        ":lib:detektYoungHarryDebugUnitTest",
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
                numberOfFindings = 1,
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
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektMain",
                        ":lib:detektOldHarryDebug",
                        ":lib:detektYoungHarryRelease",
                    )
                    .doesNotContain(
                        ":lib:detektOldHarryRelease",
                        ":lib:detektYoungHarryDebug",
                    )
            }
        }

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
            gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektOldHarryDebugAndroidTest",
                        ":lib:detektOldHarryDebugUnitTest",
                        ":lib:detektTest",
                    )
                    .doesNotContain(
                        ":lib:detektYoungHarryDebugAndroidTest",
                        ":lib:detektYoungHarryDebugUnitTest",
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
                numberOfFindings = 1,
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
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektMain",
                        ":lib:detektOldHarryDebug",
                        ":lib:detektOldHarryRelease",
                    )
                    .doesNotContain(
                        ":lib:detektYoungHarryDebug",
                        ":lib:detektYoungHarryRelease",
                    )
            }
        }

        @Test
        @DisplayName("task :lib:detektTest")
        fun libDetektTest() {
            gradleRunner.runTasksAndCheckResult(":lib:detektTest") { buildResult ->
                assertThat(buildResult.tasks.map { it.path })
                    .filteredOn { it.startsWith(":lib:detekt") }
                    .containsExactlyInAnyOrder(
                        ":lib:detektOldHarryDebugAndroidTest",
                        ":lib:detektOldHarryDebugUnitTest",
                        ":lib:detektTest",
                    )
                    .doesNotContain(
                        ":lib:detektYoungHarryDebugAndroidTest",
                        ":lib:detektYoungHarryDebugUnitTest",
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
                    numberOfFindings = 0,
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

    private fun createGradleRunnerAndSetupProject(
        projectLayout: ProjectLayout,
        dryRun: Boolean = true,
    ) = DslGradleRunner(
        projectLayout = projectLayout,
        buildFileName = "build.gradle.kts",
        gradleProperties = mapOf(
            "android.builtInKotlin" to builtInKotlinPropertyEnabled,
            "android.newDsl" to agpBuiltInKotlinUsed.toString(),
        ),
        settingsContent = """
        dependencyResolutionManagement {
            repositories {
                mavenLocal()
                mavenCentral()
                google()
            }
        }
    """.trimIndent(),
        dryRun = dryRun,
    ).also { it.setupProject() }

    private val APP_PLUGIN_BLOCK: String
        @Language("gradle.kts")
        get() = """
            plugins {
                id("com.android.application")
                ${if (!agpBuiltInKotlinUsed) """kotlin("android")""" else """"""}
                ${if (applyBuiltInKotlinPlugin.toBooleanStrict()) { """id("com.android.experimental.built-in-kotlin")""" } else { """""" }}
                id("dev.detekt")
            }
            kotlin {
                compilerOptions {
                    jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
                }
            }
        """.trimIndent()

    private val LIB_PLUGIN_BLOCK: String
        @Language("gradle.kts")
        get() = """
            plugins {
                id("com.android.library")
                ${if (!agpBuiltInKotlinUsed) """kotlin("android")""" else """"""}
                ${if (applyBuiltInKotlinPlugin.toBooleanStrict()) { """id("com.android.experimental.built-in-kotlin")""" } else { """""" }}
                id("dev.detekt")
            }
            kotlin {
                compilerOptions {
                    jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
                }
            }
        """.trimIndent()
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
private val ANDROID_BLOCK = """
    android {
        compileSdk = 34
        namespace = "dev.detekt.app"
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
""".trimIndent()

@Language("gradle.kts")
private val ANDROID_BLOCK_WITH_FLAVOR = """
    android {
        compileSdk = 34
        namespace = "dev.detekt.app"
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
    }
""".trimIndent()

@Language("gradle.kts")
private val ANDROID_BLOCK_WITH_VIEW_BINDING = """
    android {
        compileSdk = 34
        namespace = "dev.detekt.app"
        defaultConfig {
            applicationId = "dev.detekt.app"
            minSdk = 24
        }
        buildFeatures {
            viewBinding = true
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
""".trimIndent()

@Language("gradle.kts")
private val DETEKT_REPORTS_BLOCK = """
    tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
        reports {
            markdown.required.set(false)
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
    import dev.detekt.app.databinding.ActivitySampleBinding
    
    class SampleActivity : Activity() {
    
        private lateinit var binding: ActivitySampleBinding
    
        override fun onCreate(savedInstanceState: Bundle?) {
            binding = ActivitySampleBinding.inflate(LayoutInflater.from(this))
            binding.sampleView
            setContentView(binding.root)
        }
    }
    
""".trimIndent() // Last line to prevent NewLineAtEndOfFile.
