package io.github.detekt.parser

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Path

/**
 * Creates an environment instance which can be used to compile source code to KtFile's.
 * This environment also allows to modify the resulting AST files.
 */
fun createKotlinCoreEnvironment(
    configuration: CompilerConfiguration = CompilerConfiguration(),
    disposable: Disposable = Disposer.newDisposable()
): KotlinCoreEnvironment {
    // https://github.com/JetBrains/kotlin/commit/2568804eaa2c8f6b10b735777218c81af62919c1
    setIdeaIoUseFallback()
    configuration.put(
        CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
        PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false)
    )
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "detekt")

    val environment = KotlinCoreEnvironment.createForProduction(
        disposable,
        configuration,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )

    val projectCandidate = environment.project

    val project = requireNotNull(projectCandidate as? MockProject) {
        "MockProject type expected, actual - ${projectCandidate.javaClass.simpleName}"
    }

    project.registerService(PomModel::class.java, DetektPomModel(project))

    return environment
}

/**
 * Creates a compiler configuration for the kotlin compiler with all known sources and classpath jars.
 * Be aware that if any path of [pathsToAnalyze] is a directory it is scanned for java and kotlin files.
 */
fun createCompilerConfiguration(
    pathsToAnalyze: List<Path>,
    classpath: List<String>,
    languageVersion: LanguageVersion?,
    jvmTarget: JvmTarget
): CompilerConfiguration {

    val javaFiles = pathsToAnalyze.flatMap { path ->
        path.toFile().walk()
            .filter { it.isFile && it.extension.equals("java", true) }
            .toList()
    }
    val kotlinFiles = pathsToAnalyze.flatMap { path ->
        path.toFile().walk()
            .filter { it.isFile }
            .filter { it.extension.equals("kt", true) || it.extension.equals("kts", true) }
            .map { it.absolutePath }
            .toList()
    }

    val classpathFiles = classpath.map { File(it) }
    val retrievedLanguageVersion = languageVersion ?: classpathFiles.getKotlinLanguageVersion()
    val languageVersionSettings: LanguageVersionSettings? = retrievedLanguageVersion?.let {
        LanguageVersionSettingsImpl(
            languageVersion = it,
            apiVersion = ApiVersion.createByLanguageVersion(it)
        )
    }

    return CompilerConfiguration().apply {
        if (languageVersionSettings != null) {
            put(CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS, languageVersionSettings)
        }
        put(JVMConfigurationKeys.JVM_TARGET, jvmTarget)
        addJavaSourceRoots(javaFiles)
        addKotlinSourceRoots(kotlinFiles)
        addJvmClasspathRoots(classpathFiles)
    }
}

@Suppress("TooGenericExceptionCaught")
internal fun Iterable<File>.getKotlinLanguageVersion(): LanguageVersion? {
    val urls = map { it.toURI().toURL() }
    if (urls.isEmpty()) {
        return null
    }
    return URLClassLoader(urls.toTypedArray()).use { classLoader ->
        try {
            val clazz = classLoader.loadClass("kotlin.KotlinVersion")
            val field = clazz.getField("CURRENT")
            field.isAccessible = true
            val versionObj = field.get(null)
            val versionString = versionObj?.toString()
            return@use versionString?.let { LanguageVersion.fromFullVersionString(it) }
        } catch (e: Throwable) {
            return@use null // do nothing
        }
    }
}
