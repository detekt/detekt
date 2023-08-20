package io.gitlab.arturbosch.detekt.testkit

import org.intellij.lang.annotations.Language

abstract class DslTestBuilder {

    abstract val gradleBuildConfig: String
    abstract val gradleBuildName: String
    abstract val gradlePlugins: String
    abstract val gradleSubprojectsApplyPlugins: String

    @Language("gradle.kts")
    val gradleRepositories = """
        repositories {
            mavenCentral()
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
    """.trimIndent()

    @Language("gradle.kts")
    private var detektConfig: String = ""
    private var projectLayout: ProjectLayout = ProjectLayout(1)
    private var baselineFile: String? = null
    private var configFile: String? = null
    private var gradleVersion: String? = null
    private var dryRun: Boolean = false

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
            id 'java-library'
            id "io.gitlab.arturbosch.detekt"
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
        apply plugin: "io.gitlab.arturbosch.detekt"
    """.trimIndent()

    override fun toString() = "build.gradle"
}

private class KotlinBuilder : DslTestBuilder() {
    override val gradleBuildName: String = "build.gradle.kts"

    @Language("gradle.kts")
    override val gradlePlugins = """
        plugins {
            `java-library`
            id("io.gitlab.arturbosch.detekt")
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
        plugins.apply("io.gitlab.arturbosch.detekt")
    """.trimIndent()

    override fun toString() = "build.gradle.kts"
}
