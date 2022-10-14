package io.github.detekt.gradle

import io.github.detekt.gradle.extensions.KotlinCompileTaskDetektExtension
import io.github.detekt.gradle.extensions.ProjectDetektExtension
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import java.security.MessageDigest
import java.util.Base64
import java.util.Properties

class DetektKotlinCompilerPlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.pluginManager.apply(ReportingBasePlugin::class.java)

        val projectExtension = target.extensions.create(DETEKT_NAME, ProjectDetektExtension::class.java).apply {
            reportsDir.convention(
                target.extensions.getByType(ReportingExtension::class.java).baseDirectory.dir(DETEKT_NAME)
            )
            excludes.add("**/${target.relativePath(target.buildDir)}/**")

            isEnabled.convention(true)
            debug.convention(false)
            buildUponDefaultConfig.convention(true)
            allRules.convention(false)
            disableDefaultRuleSets.convention(false)
            parallel.convention(false)
            config.from(target.rootProject.layout.projectDirectory.dir(CONFIG_DIR_NAME).file(CONFIG_FILE))
        }

        target.configurations.create(CONFIGURATION_DETEKT_PLUGINS).apply {
            isVisible = false
            isTransitive = true
            description = "The $CONFIGURATION_DETEKT_PLUGINS libraries to be used for this project."
        }

        target.tasks.withType(KotlinCompile::class.java).configureEach { task ->
            task.extensions.create(DETEKT_NAME, KotlinCompileTaskDetektExtension::class.java, target).apply {
                isEnabled.convention(projectExtension.isEnabled)
                baseline.convention(projectExtension.baseline)
                debug.convention(projectExtension.debug)
                buildUponDefaultConfig.convention(projectExtension.buildUponDefaultConfig)
                allRules.convention(projectExtension.allRules)
                disableDefaultRuleSets.convention(projectExtension.disableDefaultRuleSets)
                parallel.convention(projectExtension.parallel)
                config.from(projectExtension.config)
                excludes.convention(projectExtension.excludes)
            }
        }
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        val projectExtension = project.extensions.getByType(ProjectDetektExtension::class.java)
        val taskExtension =
            kotlinCompilation.compileKotlinTask.extensions.getByType(KotlinCompileTaskDetektExtension::class.java)

        project.configurations.getByName("kotlinCompilerPluginClasspath").apply {
            extendsFrom(project.configurations.getAt(CONFIGURATION_DETEKT_PLUGINS))
        }

        val options = project.objects.listProperty(SubpluginOption::class.java).apply {
            add(SubpluginOption("debug", taskExtension.debug.get().toString()))
            add(SubpluginOption("configDigest", taskExtension.config.toDigest()))
            add(SubpluginOption("isEnabled", taskExtension.isEnabled.get().toString()))
            add(SubpluginOption("useDefaultConfig", taskExtension.buildUponDefaultConfig.get().toString()))
            add(SubpluginOption("allRules", taskExtension.allRules.get().toString()))
            add(SubpluginOption("disableDefaultRuleSets", taskExtension.disableDefaultRuleSets.get().toString()))
            add(SubpluginOption("parallel", taskExtension.parallel.get().toString()))
            add(SubpluginOption("rootDir", project.rootDir.toString()))
            add(SubpluginOption("excludes", taskExtension.excludes.get().encodeToBase64()))

            taskExtension.reports.all { report ->
                report.enabled.convention(true)
                report.destination.convention(
                    projectExtension.reportsDir.file("${kotlinCompilation.name}.${report.name}")
                )

                if (report.enabled.get()) {
                    add(
                        SubpluginOption(
                            "report",
                            "${report.name}:${report.destination.asFile.get().absolutePath}"
                        )
                    )
                }
            }
        }

        taskExtension.baseline.getOrNull()?.let { options.add(SubpluginOption("baseline", it.toString())) }
        if (taskExtension.config.any()) {
            options.add(SubpluginOption("config", taskExtension.config.joinToString(",")))
        }

        return options
    }

    override fun getCompilerPluginId(): String = DETEKT_COMPILER_PLUGIN

    override fun getPluginArtifact(): SubpluginArtifact {
        val props = Properties()
        val inputStream = javaClass.classLoader.getResourceAsStream("versions.properties")

        inputStream.use { props.load(it) }
        val version = props.getProperty("detektCompilerPluginVersion")

        return SubpluginArtifact("io.github.detekt", "detekt-compiler-plugin", version)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.platformType in setOf(KotlinPlatformType.jvm, KotlinPlatformType.androidJvm)
}

internal fun ConfigurableFileCollection.toDigest(): String {
    val concatenatedConfig = this
        .filter { it.isFile }
        .map(File::readBytes)
        .fold(byteArrayOf()) { acc, file -> acc + file }

    return Base64.getEncoder().encodeToString(
        MessageDigest.getInstance("SHA-256").digest(concatenatedConfig)
    )
}

private fun Set<String>.encodeToBase64(): String {
    val os = ByteArrayOutputStream()

    ObjectOutputStream(os).use { oos ->
        oos.writeInt(size)
        forEach(oos::writeUTF)
        oos.flush()
    }

    return Base64.getEncoder().encodeToString(os.toByteArray())
}
