package dev.detekt.core.parser

import org.jetbrains.kotlin.cli.common.arguments.CommonCompilerArgumentsConfigurator
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.parseCommandLineArguments
import org.jetbrains.kotlin.cli.common.arguments.toLanguageVersionSettings
import org.jetbrains.kotlin.cli.common.arguments.validateArguments
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.create
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.config.configureJdkClasspathRoots
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.MessageCollectorAccess
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.config.messageCollector
import org.jetbrains.kotlin.diagnostics.KtSourcelessDiagnosticFactory
import java.io.File
import java.io.PrintStream
import java.nio.file.Path

/**
 * Creates a compiler configuration for the kotlin compiler with all known sources and classpath jars.
 * Be aware that if any path of [pathsToAnalyze] is a directory it is scanned for java and kotlin files.
 */
@Suppress("LongParameterList")
@OptIn(MessageCollectorAccess::class)
fun createCompilerConfiguration(
    pathsToAnalyze: List<Path>,
    classpath: List<Path>,
    apiVersion: String?,
    languageVersion: String?,
    jvmTarget: String,
    jdkHome: Path?,
    freeCompilerArgs: List<String>,
    printStream: PrintStream,
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

    val classpathFiles = classpath.map(Path::toFile)

    val jvmCompilerArguments = K2JVMCompilerArguments()

    val args = buildList {
        if (apiVersion != null) {
            add("-api-version")
            add(apiVersion)
        }
        if (languageVersion != null) {
            add("-language-version")
            add(languageVersion)
        }
        add("-jvm-target")
        add(jvmTarget)
        addAll(freeCompilerArgs)
    }

    parseCommandLineArguments(args, jvmCompilerArguments)

    validateArguments(jvmCompilerArguments.errors)?.let { throw IllegalStateException(it) }

    val collector = PrintStreamMessageCollector(printStream)

    return CompilerConfiguration.create().apply {
        messageCollector = collector
        addJavaSourceRoots(javaFiles)
        addKotlinSourceRoots(kotlinFiles)
        addJvmClasspathRoots(classpathFiles)
        languageVersionSettings = jvmCompilerArguments.toLanguageVersionSettings(PrintingReporter(printStream))
        val parsedJvmTarget = requireNotNull(JvmTarget.fromString(checkNotNull(jvmCompilerArguments.jvmTarget))) {
            "Unknown JVM target version: $jvmTarget, supported versions: ${JvmTarget.supportedValues()}"
        }
        put(JVMConfigurationKeys.JVM_TARGET, parsedJvmTarget)
        jvmCompilerArguments.friendPaths?.let { put(JVMConfigurationKeys.FRIEND_PATHS, it.toList()) }

        if (jdkHome != null) {
            put(JVMConfigurationKeys.JDK_HOME, jdkHome.toFile())
        } else {
            put(JVMConfigurationKeys.JDK_HOME, File(System.getProperty("java.home")))
        }

        configureJdkClasspathRoots()
    }
}

private class PrintStreamMessageCollector(private val printStream: PrintStream) : MessageCollector {
    private var hasErrors = false

    override fun clear() {
        hasErrors = false
    }

    override fun hasErrors(): Boolean = hasErrors

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        hasErrors = hasErrors || severity.isError
        printStream.println(renderCompilerMessage(severity, message, location))
    }
}

private class PrintingReporter(private val printStream: PrintStream) : CommonCompilerArgumentsConfigurator.Reporter {
    override fun reportError(message: String) {
        printStream.println("error: $message")
    }

    override fun reportWarning(message: String) {
        printStream.println("warning: $message")
    }

    override fun info(message: String) {
        printStream.println("info: $message")
    }

    override fun report(diagnosticFactory: KtSourcelessDiagnosticFactory, message: String) {
        printStream.println(message)
    }

    override fun withLanguageVersionSettings(
        settings: LanguageVersionSettings,
    ): CommonCompilerArgumentsConfigurator.Reporter = this
}
