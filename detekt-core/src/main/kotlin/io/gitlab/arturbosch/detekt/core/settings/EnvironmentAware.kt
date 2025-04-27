package io.gitlab.arturbosch.detekt.core.settings

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.pom.PomModel
import io.github.detekt.parser.DetektPomModel
import io.github.detekt.parser.createCompilerConfiguration
import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.LoggingSpec
import io.github.detekt.tooling.api.spec.ProjectSpec
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.cli.common.CliModuleVisibilityManagerImpl
import org.jetbrains.kotlin.cli.jvm.compiler.CliTraceHolder
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.friendPaths
import org.jetbrains.kotlin.load.kotlin.ModuleVisibilityManager
import org.jetbrains.kotlin.resolve.CodeAnalyzerInitializer
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
    projectSpec: ProjectSpec,
    compilerSpec: CompilerSpec,
    loggingSpec: LoggingSpec,
) : AutoCloseable, Closeable, EnvironmentAware {

    private val printStream = if (loggingSpec.debug) loggingSpec.errorChannel.asPrintStream() else NullPrintStream
    override val configuration: CompilerConfiguration =
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

    override val disposable: Disposable = Disposer.newDisposable()

    private val analysisSession = buildStandaloneAnalysisAPISession(disposable) {
        // Required for autocorrect support
        registerProjectService(PomModel::class.java, DetektPomModel)

        // Required by K1 compiler setup
        registerProjectService(CodeAnalyzerInitializer::class.java, CliTraceHolder(project))
        registerProjectService(ModuleVisibilityManager::class.java, CliModuleVisibilityManagerImpl(true))
        val moduleVisibilityManager = ModuleVisibilityManager.SERVICE.getInstance(project)
        configuration.friendPaths.forEach(moduleVisibilityManager::addFriendPath)

        configuration.putIfAbsent(CommonConfigurationKeys.MODULE_NAME, "<no module name provided>")

        @Suppress("DEPRECATION") // Required until fully transitioned to setting up Kotlin Analysis API session
        buildKtModuleProviderByCompilerConfiguration(configuration)
    }

    override val project: Project by lazy {
        analysisSession.project
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
