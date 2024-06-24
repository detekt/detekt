package io.gitlab.arturbosch.detekt.core.settings

import io.github.detekt.parser.createCompilerConfiguration
import io.github.detekt.parser.createKotlinCoreEnvironment
import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.LoggingSpec
import io.github.detekt.tooling.api.spec.ProjectSpec
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import java.io.Closeable
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

interface EnvironmentAware {

    val disposable: Disposable
    val classpath: List<String>
    val environment: KotlinCoreEnvironment
}

internal class EnvironmentFacade(
    private val projectSpec: ProjectSpec,
    private val compilerSpec: CompilerSpec,
    private val loggingSpec: LoggingSpec,
) : AutoCloseable, Closeable, EnvironmentAware {

    override val disposable: Disposable = Disposer.newDisposable()

    override val classpath: List<String> = compilerSpec.classpathEntries()

    override val environment: KotlinCoreEnvironment by lazy {
        val compilerConfiguration = createCompilerConfiguration(
            projectSpec.inputPaths.toList(),
            classpath,
            compilerSpec.parseApiVersion(),
            compilerSpec.parseLanguageVersion(),
            compilerSpec.parseJvmTarget(),
            compilerSpec.jdkHome,
        )
        createKotlinCoreEnvironment(
            compilerConfiguration,
            disposable,
            if (loggingSpec.debug) loggingSpec.errorChannel.asPrintStream() else NullPrintStream,
        )
    }

    override fun close() {
        Disposer.dispose(disposable)
    }
}

internal fun CompilerSpec.classpathEntries(): List<String> =
    classpath?.split(File.pathSeparator).orEmpty()

internal fun CompilerSpec.parseApiVersion(): ApiVersion? {
    fun parse(value: String): ApiVersion {
        val version = ApiVersion.parse(value)
        return checkNotNull(version) { "Invalid value passed as API version." }
    }
    return apiVersion?.let(::parse)
}

internal fun CompilerSpec.parseLanguageVersion(): LanguageVersion? {
    fun parse(value: String): LanguageVersion {
        val version = LanguageVersion.fromFullVersionString(value)
        return checkNotNull(version) { "Invalid value passed as language version." }
    }
    return languageVersion?.let(::parse)
}

internal fun CompilerSpec.parseJvmTarget(): JvmTarget =
    checkNotNull(JvmTarget.fromString(jvmTarget)) {
        "Invalid value ($jvmTarget) passed to --jvm-target," +
            " must be one of ${JvmTarget.entries.map(JvmTarget::description)}"
    }

private object NullPrintStream : PrintStream(
    object : OutputStream() {
        override fun write(b: Int) {
            // no-op
        }
    }
)

private fun Appendable.asPrintStream(): PrintStream {
    val appendable = this
    return if (appendable is PrintStream) {
        appendable
    } else {
        PrintStream(
            object : OutputStream() {
                override fun write(b: Int) {
                    appendable.append(b.toChar())
                }
            }
        )
    }
}
