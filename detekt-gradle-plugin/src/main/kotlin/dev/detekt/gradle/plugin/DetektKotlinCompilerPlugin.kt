package dev.detekt.gradle.plugin

import dev.detekt.detekt_gradle_plugin.BuildConfig
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.extensions.KotlinCompileTaskDetektExtension
import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.DETEKT_EXTENSION
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import java.security.MessageDigest
import java.util.Base64

class DetektKotlinCompilerPlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.pluginManager.apply(DetektBasePlugin::class.java)

        val extension = target.extensions.getByType(DetektExtension::class.java)

        target.tasks.withType(KotlinJvmCompile::class.java).configureEach { task ->
            task.extensions.create(DETEKT_EXTENSION, KotlinCompileTaskDetektExtension::class.java, target).apply {
                isEnabled.convention(extension.enableCompilerPlugin)
                baseline.convention(extension.baseline)
                debug.convention(extension.debug)
                buildUponDefaultConfig.convention(extension.buildUponDefaultConfig)
                allRules.convention(extension.allRules)
                disableDefaultRuleSets.convention(extension.disableDefaultRuleSets)
                parallel.convention(extension.parallel)
                config.from(extension.config)
                excludes.convention(DetektPlugin.defaultExcludes)
            }
        }
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project

        val projectExtension = project.extensions.getByType(DetektExtension::class.java)

        val taskExtension = kotlinCompilation.compileTaskProvider.map {
            it.extensions.getByType(KotlinCompileTaskDetektExtension::class.java)
        }

        project.configurations.getByName("kotlinCompilerPluginClasspath")
            .extendsFrom(project.configurations.getAt(CONFIGURATION_DETEKT_PLUGINS))

        val options = project.objects.listProperty(SubpluginOption::class.java).apply {
            add(SubpluginOption("debug", taskExtension.get().debug.toString()))
            add(SubpluginOption("configDigest", taskExtension.get().config.toDigest()))
            add(SubpluginOption("isEnabled", taskExtension.get().isEnabled.getOrElse(false).toString()))
            add(SubpluginOption("useDefaultConfig", taskExtension.get().buildUponDefaultConfig.get().toString()))
            add(SubpluginOption("allRules", taskExtension.get().allRules.get().toString()))
            add(SubpluginOption("disableDefaultRuleSets", taskExtension.get().disableDefaultRuleSets.get().toString()))
            add(SubpluginOption("parallel", taskExtension.get().parallel.get().toString()))
            add(SubpluginOption("rootDir", project.rootDir.toString()))
            add(SubpluginOption("excludes", taskExtension.get().excludes.get().encodeToBase64()))

            taskExtension.get().reports.all { report ->
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

        taskExtension.get().baseline.getOrNull()?.let { options.add(SubpluginOption("baseline", it.toString())) }
        taskExtension.get().config.forEach {
            options.add(SubpluginOption("config", it.absolutePath))
        }

        return options
    }

    override fun getCompilerPluginId(): String = "detekt-compiler-plugin"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("dev.detekt", "detekt-compiler-plugin", BuildConfig.DETEKT_COMPILER_PLUGIN_VERSION)

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
