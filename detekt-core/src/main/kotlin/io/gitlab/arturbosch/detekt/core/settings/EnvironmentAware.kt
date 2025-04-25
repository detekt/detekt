package io.gitlab.arturbosch.detekt.core.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import io.github.detekt.parser.createCompilerConfiguration
import io.github.detekt.parser.createKotlinCoreEnvironment
import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.LoggingSpec
import io.github.detekt.tooling.api.spec.ProjectSpec
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.load.kotlin.ModuleVisibilityManager
import java.io.Closeable
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

interface EnvironmentAware {
    val project: Project
    val configuration: CompilerConfiguration
    val disposable: Disposable
}

internal class EnvironmentFacade(
    private val projectSpec: ProjectSpec,
    private val compilerSpec: CompilerSpec,
    loggingSpec: LoggingSpec,
) : AutoCloseable, Closeable, EnvironmentAware {

    private val printStream = if (loggingSpec.debug) loggingSpec.errorChannel.asPrintStream() else NullPrintStream
    private val environment: KotlinCoreEnvironment by lazy {
        val env = createKotlinCoreEnvironment(
            configuration,
            disposable,
            printStream,
        )

        val moduleVisibilityManager = ModuleVisibilityManager.SERVICE.getInstance(env.project)
        configuration.getList(JVMConfigurationKeys.FRIEND_PATHS).forEach(moduleVisibilityManager::addFriendPath)

        env
    }

    override val disposable: Disposable = Disposer.newDisposable()

    override val project: Project by lazy {
        environment.project
    }

    override val configuration: CompilerConfiguration by lazy {
        createCompilerConfiguration(
            projectSpec.inputPaths.toList(),
            compilerSpec.classpathEntries(),
            compilerSpec.apiVersion,
            compilerSpec.languageVersion,
            compilerSpec.jvmTarget,
            compilerSpec.jdkHome,
            compilerSpec.freeCompilerArgs,
            printStream,
        )
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
