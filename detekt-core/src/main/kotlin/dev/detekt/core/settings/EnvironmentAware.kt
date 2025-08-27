package dev.detekt.core.settings

import com.google.devtools.ksp.standalone.buildKspLibraryModule
import com.google.devtools.ksp.standalone.buildKspSdkModule
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.pom.PomModel
import dev.detekt.parser.DetektPomModel
import dev.detekt.parser.createCompilerConfiguration
import dev.detekt.tooling.api.spec.CompilerSpec
import dev.detekt.tooling.api.spec.LoggingSpec
import dev.detekt.tooling.api.spec.ProjectSpec
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.projectStructure.KaSourceModule
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.cli.common.CliModuleVisibilityManagerImpl
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.jvm.compiler.CliTraceHolder
import org.jetbrains.kotlin.cli.jvm.config.jvmClasspathRoots
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.friendPaths
import org.jetbrains.kotlin.config.jdkHome
import org.jetbrains.kotlin.config.jvmTarget
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.load.kotlin.ModuleVisibilityManager
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.CodeAnalyzerInitializer
import java.io.Closeable
import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import java.util.UUID
import kotlin.io.path.Path

interface EnvironmentAware {
    val project: Project
    val configuration: CompilerConfiguration
    val ktFiles: List<KtFile>
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

    private lateinit var sourceModule: KaSourceModule

    @OptIn(KaExperimentalApi::class)
    override val ktFiles: List<KtFile>
        get() = sourceModule.psiRoots.filterIsInstance<KtFile>()

    private val analysisSession = buildStandaloneAnalysisAPISession(disposable) {
        // Required for autocorrect support
        registerProjectService(PomModel::class.java, DetektPomModel)

        // Required by K1 compiler setup
        registerProjectService(CodeAnalyzerInitializer::class.java, CliTraceHolder(project))
        registerProjectService(ModuleVisibilityManager::class.java, CliModuleVisibilityManagerImpl(true))
        val moduleVisibilityManager = ModuleVisibilityManager.SERVICE.getInstance(project)
        configuration.friendPaths.forEach(moduleVisibilityManager::addFriendPath)

        configuration.putIfAbsent(CommonConfigurationKeys.MODULE_NAME, "<no module name provided>")

        buildKtModuleProvider {
            val targetPlatform =
                JvmPlatforms.jvmPlatformByTargetVersion(configuration.jvmTarget ?: JvmTarget.DEFAULT)
            platform = targetPlatform

            val jdk = configuration.jdkHome?.let { jdkHome ->
                buildKspSdkModule {
                    addBinaryRootsFromJdkHome(jdkHome.toPath(), isJre = false)
                    platform = targetPlatform
                    libraryName = "jdk"
                }
            }

            val friends = configuration.friendPaths.map {
                buildKspLibraryModule {
                    platform = targetPlatform
                    addBinaryRoot(Path(it))
                    libraryName = UUID.randomUUID().toString()
                }
            }

            val dependencies = configuration.jvmClasspathRoots.map {
                buildKspLibraryModule {
                    platform = targetPlatform
                    addBinaryRoot(it.toPath())
                    libraryName = "regulardependencies"
                }
            }

            sourceModule = buildKtSourceModule {
                addSourceRoots(configuration.kotlinSourceRoots.map { Path(it.path) })
                platform = targetPlatform
                moduleName = "source"

                jdk?.let { addRegularDependency(it) }
                friends.forEach {
                    // Friend dependencies must also be declared as regular dependencies - https://github.com/JetBrains/kotlin/commit/69cfa0498a76f0c3eec39eb06b5de70a0d06e41a
                    addFriendDependency(it)
                    addRegularDependency(it)
                }
                dependencies.forEach {
                    addRegularDependency(it)
                }

                languageVersionSettings = configuration.languageVersionSettings
            }

            addModule(sourceModule)
        }
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
