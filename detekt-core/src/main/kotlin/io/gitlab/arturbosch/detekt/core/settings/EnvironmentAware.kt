package io.gitlab.arturbosch.detekt.core.settings

import io.github.detekt.parser.createCompilerConfiguration
import io.github.detekt.parser.createKotlinCoreEnvironment
import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.LoggingSpec
import io.github.detekt.tooling.api.spec.ProjectSpec
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.load.kotlin.ModuleVisibilityManager
import java.io.Closeable
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

interface EnvironmentAware {

    val disposable: Disposable
    val environment: KotlinCoreEnvironment
}

internal class EnvironmentFacade(
    private val projectSpec: ProjectSpec,
    private val compilerSpec: CompilerSpec,
    private val loggingSpec: LoggingSpec,
) : AutoCloseable, Closeable, EnvironmentAware {

    override val disposable: Disposable = Disposer.newDisposable()

    override val environment: KotlinCoreEnvironment by lazy {
        val printStream = if (loggingSpec.debug) loggingSpec.errorChannel.asPrintStream() else NullPrintStream
        val compilerConfiguration = createCompilerConfiguration(
            projectSpec.inputPaths.toList(),
            compilerSpec.classpathEntries(),
            compilerSpec.apiVersion,
            compilerSpec.languageVersion,
            compilerSpec.jvmTarget,
            compilerSpec.jdkHome,
            compilerSpec.freeCompilerArgs,
            printStream,
        )
        val env = createKotlinCoreEnvironment(
            compilerConfiguration,
            disposable,
            printStream,
        )

        val moduleVisibilityManager = ModuleVisibilityManager.SERVICE.getInstance(env.project)
        compilerConfiguration.getList(JVMConfigurationKeys.FRIEND_PATHS).forEach(moduleVisibilityManager::addFriendPath)

        env
    }

    override fun close() {
        Disposer.dispose(disposable)
    }
}

internal fun CompilerSpec.classpathEntries(): List<String> =
    classpath?.split(File.pathSeparator).orEmpty()

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
