package io.gitlab.arturbosch.detekt.testkit

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Files
import java.util.UUID

@Suppress("TooManyFunctions", "ClassOrdering")
class DslGradleRunner @Suppress("LongParameterList") constructor(
    val projectLayout: ProjectLayout,
    val buildFileName: String,
    val mainBuildFileContent: String,
    val configFileOrNone: String? = null,
    val baselineFiles: List<String> = emptyList(),
    val gradleVersionOrNone: String? = null,
    val dryRun: Boolean = false
) {

    private val rootDir: File = Files.createTempDirectory("applyPlugin").toFile().apply { deleteOnExit() }
    private val randomString = UUID.randomUUID().toString()

    private val settingsContent = """
        |rootProject.name = "rootDir-project"
        |include(${projectLayout.submodules.joinToString(",") { "\"${it.name}\"" }})
        |
        """.trimMargin()

    private val baselineContent = """
        |<some>
        |   <xml/>
        |</some>
        """.trimMargin()

    private val configFileContent = """
        |build:
        |  maxIssues: 5
        |style:
        |  MagicNumber:
        |    active: true
        """.trimMargin()

    /**
     * Each generated file is different so the artifacts are not cached in between test runs
     */
    private fun ktFileContent(className: String, withCodeSmell: Boolean = false) = """
    |internal class $className(
    |   val randomDefaultValue: String = "$randomString"
    |) {
    |   val smellyConstant: Int = ${if (withCodeSmell) "11" else "0"}
    |}
    |
    """.trimMargin()

    fun setupProject() {
        writeProjectFile(buildFileName, mainBuildFileContent)
        writeProjectFile(SETTINGS_FILENAME, settingsContent)
        configFileOrNone?.let { writeProjectFile(configFileOrNone, configFileContent) }
        baselineFiles.forEach { file -> writeProjectFile(file, baselineContent) }
        projectLayout.srcDirs.forEachIndexed { srcDirIdx, sourceDir ->
            repeat(projectLayout.numberOfSourceFilesInRootPerSourceDir) { srcFileIndex ->
                val withCodeSmell =
                    srcDirIdx * projectLayout.numberOfSourceFilesInRootPerSourceDir +
                        srcFileIndex < projectLayout.numberOfCodeSmellsInRootPerSourceDir
                writeKtFile(
                    dir = File(rootDir, sourceDir),
                    className = "My${srcDirIdx}Root${srcFileIndex}Class",
                    withCodeSmell = withCodeSmell
                )
            }
        }

        projectLayout.submodules.forEach { submodule ->
            submodule.writeModuleFile(buildFileName, submodule.buildFileContent ?: "")
            submodule.baselineFiles.forEach { file -> submodule.writeModuleFile(file, baselineContent) }
            submodule.srcDirs.forEachIndexed { srcDirIdx, moduleSourceDir ->
                repeat(submodule.numberOfSourceFilesPerSourceDir) {
                    val withCodeSmell =
                        srcDirIdx * submodule.numberOfSourceFilesPerSourceDir + it < submodule.numberOfCodeSmells
                    writeKtFile(
                        dir = File(submodule.moduleRoot, moduleSourceDir),
                        className = "My$srcDirIdx${submodule.name}${it}Class",
                        withCodeSmell = withCodeSmell
                    )
                }
            }
        }
    }

    fun projectFile(path: String): File = File(rootDir, path).canonicalFile

    fun writeProjectFile(filename: String, content: String) {
        File(rootDir, filename).writeText(content)
    }

    fun writeKtFile(srcDir: String, className: String) {
        writeKtFile(File(rootDir, srcDir), className)
    }

    fun Submodule.projectFile(path: String): File = File(moduleRoot, path).canonicalFile

    private fun writeKtFile(dir: File, className: String, withCodeSmell: Boolean = false) {
        dir.mkdirs()
        File(dir, "$className.kt").writeText(ktFileContent(className, withCodeSmell))
    }

    private fun Submodule.writeModuleFile(filename: String, content: String) {
        File(moduleRoot, filename).writeText(content)
    }

    private val Submodule.moduleRoot: File
        get() = File(rootDir, name).apply { mkdirs() }

    @OptIn(ExperimentalStdlibApi::class)
    private fun buildGradleRunner(tasks: List<String>): GradleRunner {
        val args = buildList<String> {
            add("--stacktrace")
            add("--info")
            add("--build-cache")
            add("-Dorg.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m")
            if (dryRun) {
                add("-Pdetekt-dry-run=true")
            }
            addAll(tasks.toList())
        }

        return GradleRunner.create().apply {
            withProjectDir(rootDir)
            withPluginClasspath()
            withArguments(args)
            gradleVersionOrNone?.let { withGradleVersion(gradleVersionOrNone) }
        }
    }

    fun runTasksAndCheckResult(vararg tasks: String, doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        this.doAssert(runTasks(*tasks))
    }

    fun runTasks(vararg tasks: String): BuildResult = buildGradleRunner(tasks.toList()).build()

    fun runTasksAndExpectFailure(vararg tasks: String, doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        val result: BuildResult = buildGradleRunner(tasks.toList()).buildAndFail()
        this.doAssert(result)
    }

    fun runDetektTaskAndCheckResult(doAssert: DslGradleRunner.(BuildResult) -> Unit) {
        runTasksAndCheckResult(DETEKT_TASK) { this.doAssert(it) }
    }

    fun runDetektTask(): BuildResult = runTasks(DETEKT_TASK)

    fun runDetektTaskAndExpectFailure(doAssert: DslGradleRunner.(BuildResult) -> Unit = {}) {
        val result = buildGradleRunner(listOf(DETEKT_TASK)).buildAndFail()
        this.doAssert(result)
    }

    companion object {
        const val SETTINGS_FILENAME = "settings.gradle"
        private const val DETEKT_TASK = "detekt"
    }
}

fun DslGradleRunner.createJavaClass(name: String) {
    projectFile("${projectLayout.srcDirs.first { it.contains("main") }}/$name.java")
        .apply { createNewFile() }
        .writeText("public class $name {}")
    projectFile("${projectLayout.srcDirs.first { it.contains("test") }}/${name}Test.java")
        .apply { createNewFile() }
        .writeText("public class ${name}Test {}")
}

fun DslGradleRunner.createJavaClass(submodule: Submodule, name: String) {
    submodule.projectFile("${submodule.srcDirs.first { it.contains("main") }}/$name.java")
        .apply { createNewFile() }
        .writeText("public class $name {}")
    submodule.projectFile("${submodule.srcDirs.first { it.contains("test") }}/${name}Test.java")
        .apply { createNewFile() }
        .writeText("public class ${name}Test {}")
}
