package dev.detekt.gradle.plugin.internal

import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.tasks.VerificationException
import org.gradle.util.GradleVersion

internal fun Project.rootProjectDirectoryCompat(): Directory =
    if (GradleVersion.current() >= GradleVersion.version("8.8")) {
        isolated.rootProject.projectDirectory
    } else {
        rootProject.layout.projectDirectory
    }

internal fun ConfigurableFileCollection.conventionCompat(paths: Iterable<*>): ConfigurableFileCollection =
    if (GradleVersion.current() >= GradleVersion.version("8.8")) {
        convention(paths)
    } else {
        setFrom(paths)
        this
    }

internal fun ConfigurableFileCollection.conventionCompat(vararg paths: Any): ConfigurableFileCollection =
    if (GradleVersion.current() >= GradleVersion.version("8.8")) {
        convention(paths)
    } else {
        setFrom(paths)
        this
    }

@Suppress("NOTHING_TO_INLINE") // not inlining for performance, but for simpler stack traces
internal inline fun verificationExceptionCompat(message: String, cause: Throwable): GradleException =
    if (GradleVersion.current() >= GradleVersion.version("8.2")) {
        VerificationException(message, cause)
    } else {
        VerificationException(message)
    }

@Suppress("UnstableApiUsage")
internal fun ConfigurationContainer.declarableCompat(
    name: String,
    configure: Configuration.() -> Unit = {},
): NamedDomainObjectProvider<out Configuration> =
    when {
        GradleVersion.current() >= GradleVersion.version("8.5") -> dependencyScope(name, configure)

        else -> register(name) { config ->
            config.isCanBeResolved = false
            config.isCanBeConsumed = false
            configure(config)
        }
    }

@Suppress("UnstableApiUsage")
internal fun ConfigurationContainer.resolvableCompat(
    name: String,
    declarable: NamedDomainObjectProvider<out Configuration>,
    configure: Configuration.() -> Unit = {},
): NamedDomainObjectProvider<out Configuration> =
    when {
        GradleVersion.current() >= GradleVersion.version("8.5") -> resolvable(name) { config ->
            config.extendsFrom(declarable.get())
            configure(config)
        }

        else -> register(name) { config ->
            config.extendsFrom(declarable.get())
            if (GradleVersion.current() >= GradleVersion.version("8.2")) {
                config.isCanBeDeclared = false
            }
            config.isCanBeResolved = true
            config.isCanBeConsumed = false
            configure(config)
        }
    }
