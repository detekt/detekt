package dev.detekt.gradle.testkit

import org.intellij.lang.annotations.Language
import java.io.File

abstract class DslTestBuilder {

    abstract val gradleBuildConfig: String
    abstract val gradleBuildName: String
    abstract val gradlePlugins: String
    abstract val gradleSubprojectsApplyPlugins: String

    @Language("gradle.kts")
    val gradleRepositories = """
        repositories {
            mavenLocal()
            mavenCentral()
        }
    """.trimIndent()

    @Language("gradle.kts")
    private var detektConfig: String = ""
    private var projectLayout: ProjectLayout = ProjectLayout(1)
    private var baselineFile: String? = null
    private var configFile: String? = null
    private var gradleVersion: String? = null
    private var dryRun: Boolean = false
    private val customPluginClasspath: MutableList<File> = mutableListOf()

    fun withDetektConfig(@Language("gradle.kts") config: String): DslTestBuilder {
        detektConfig = config
        return this
    }

    fun withProjectLayout(layout: ProjectLayout): DslTestBuilder {
        projectLayout = layout
        return this
    }

    fun withBaseline(filename: String): DslTestBuilder {
        baselineFile = filename
        return this
    }

    fun withConfigFile(filename: String): DslTestBuilder {
        configFile = filename
        return this
    }

    fun withGradleVersion(version: String): DslTestBuilder {
        gradleVersion = version
        return this
    }

    fun withPluginClasspath(files: Collection<File>): DslTestBuilder {
        customPluginClasspath.addAll(files)
        return this
    }

    fun dryRun(): DslTestBuilder {
        dryRun = true
        return this
    }

    fun build(): DslGradleRunner {
        val runner = DslGradleRunner(
            projectLayout = projectLayout,
            buildFileName = gradleBuildName,
            mainBuildFileContent = joinGradleBlocks(gradleBuildConfig, detektConfig),
            configFileOrNone = configFile,
            baselineFiles = baselineFile?.let { listOf(it) }.orEmpty(),
            gradleVersionOrNone = gradleVersion,
            dryRun = dryRun,
            customPluginClasspath = customPluginClasspath,
        )
        runner.setupProject()
        return runner
    }

    companion object {
        fun kotlin(): DslTestBuilder = KotlinBuilder()
        fun groovy(): DslTestBuilder = GroovyBuilder()
    }
}

private class GroovyBuilder : DslTestBuilder() {
    override val gradleBuildName: String = "build.gradle"

    @Language("gradle")
    override val gradlePlugins = """
        plugins {
            id 'org.jetbrains.kotlin.jvm'
            id "dev.detekt"
        }
    """.trimIndent()

    @Language("gradle")
    private val dependencies = """
        dependencies {
            implementation "org.jetbrains.kotlin:kotlin-stdlib"
        }
    """.trimIndent()

    @Language("gradle")
    override val gradleBuildConfig: String = joinGradleBlocks(gradlePlugins, gradleRepositories, dependencies)

    @Language("gradle")
    override val gradleSubprojectsApplyPlugins = """
        apply plugin: "dev.detekt"
    """.trimIndent()

    override fun toString() = "build.gradle"
}

private class KotlinBuilder : DslTestBuilder() {
    override val gradleBuildName: String = "build.gradle.kts"

    @Language("gradle.kts")
    override val gradlePlugins = """
        plugins {
            kotlin("jvm")
            id("dev.detekt")
        }
    """.trimIndent()

    @Language("gradle.kts")
    private val dependencies = """
        dependencies {
            implementation(kotlin("stdlib"))
        }
    """.trimIndent()

    @Language("gradle.kts")
    override val gradleBuildConfig: String = joinGradleBlocks(gradlePlugins, gradleRepositories, dependencies)

    @Language("gradle.kts")
    override val gradleSubprojectsApplyPlugins = """
        plugins.apply("dev.detekt")
    """.trimIndent()

    override fun toString() = "build.gradle.kts"
}
