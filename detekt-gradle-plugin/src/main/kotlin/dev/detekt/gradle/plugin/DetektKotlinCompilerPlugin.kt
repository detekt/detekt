package dev.detekt.gradle.plugin

import dev.detekt.gradle.plugin.DetektBasePlugin.Companion.DETEKT_EXTENSION
import dev.detekt.gradle.plugin.extensions.DetektExtension
import dev.detekt.gradle.plugin.extensions.KotlinCompileTaskDetektExtension
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Provider
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
        target.pluginManager.apply(DetektBasePlugin::class.java)

        val extension = target.extensions.getByType(DetektExtension::class.java)

        target.tasks.withType(KotlinCompile::class.java).configureEach { task ->
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

    override fun getPluginArtifact(): SubpluginArtifact {
        // Other Gradle plugins can also have a versions.properties.
        val distinctVersions = this::class
            .java
            .classLoader
            .getResources("detekt-versions.properties")
            .toList()
            .mapNotNull { versions ->
                Properties().run {
                    val inputStream = versions.openConnection()
                        /*
                         * Due to https://bugs.openjdk.java.net/browse/JDK-6947916 and https://bugs.openjdk.java.net/browse/JDK-8155607,
                         * it is necessary to disallow caches to maintain stability on JDK 8 and 11 (and possibly more).
                         * Otherwise, simultaneous invocations of detekt in the same VM can fail spuriously. A similar bug is referenced in
                         * https://github.com/detekt/detekt/issues/3396. The performance regression is likely unnoticeable.
                         * Due to https://github.com/detekt/detekt/issues/4332 it is included for all JDKs.
                         */
                        .apply { useCaches = false }
                        .getInputStream()
                    load(inputStream)
                    getProperty("detektCompilerPluginVersion")
                }
            }
            .distinct()
        val version = distinctVersions.singleOrNull() ?: error(
            "You're importing two detekt compiler plugins which have different versions. " +
                "(${distinctVersions.joinToString()}) Make sure to align the versions."
        )

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
