package io.github.detekt.parser

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.EXCEPTION
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.LOGGING
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.prepareJvmSessions
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.FirKotlinToJvmBytecodeCompiler.FrontendContext
import org.jetbrains.kotlin.cli.jvm.compiler.FirKotlinToJvmBytecodeCompiler.createPendingReporter
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.VfsBasedProjectEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.createContextForIncrementalCompilation
import org.jetbrains.kotlin.cli.jvm.compiler.createLibraryListForJvm
import org.jetbrains.kotlin.cli.jvm.compiler.messageCollector
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.diagnostics.impl.BaseDiagnosticsCollector
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.pipeline.FirResult
import org.jetbrains.kotlin.fir.pipeline.buildResolveAndCheckFirFromKtFiles
import org.jetbrains.kotlin.load.kotlin.incremental.components.IncrementalCompilationComponents
import org.jetbrains.kotlin.modules.TargetId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.multiplatform.hmppModuleName
import org.jetbrains.kotlin.resolve.multiplatform.isCommonSource
import java.io.File

/**
 * Loads classes using the compiler tools into the Frontend Intermediate Representation (FIR) for inspection.
 *
 * Based on:
 * https://github.com/cashapp/zipline/blob/4fa1014c833c46fd8c4b6b6add83786a2e4ea618/zipline-api-validator/src/main/kotlin/app/cash/zipline/api/validator/fir/KotlinFirLoader.kt
 * and
 * https://github.com/cashapp/redwood/blob/afe1c9f5f95eec3cff46837a4b2749cbaf72af8b/redwood-tooling-schema/src/main/kotlin/app/cash/redwood/tooling/schema/schemaParserFir.kt
 */
class KotlinFirLoader(
    private val sources: Collection<File>,
    private val classpath: Collection<File>,
    private val compiler: KtCompiler,
) : AutoCloseable {
    private val disposable = Disposer.newDisposable()

    private val messageCollector = object : MessageCollector {
        override fun clear() = Unit
        override fun hasErrors() = false

        override fun report(
            severity: CompilerMessageSeverity,
            message: String,
            location: CompilerMessageSourceLocation?,
        ) {
            val destination = when (severity) {
                LOGGING -> null
                EXCEPTION, ERROR -> System.err
                else -> System.out
            }
            destination?.println(message)
        }
    }

    /**
     * @param targetName an opaque identifier for this operation.
     */
    fun load(targetName: String = "unnamed"): FirResult {
        val configuration = CompilerConfiguration()
        configuration.put(CommonConfigurationKeys.MODULE_NAME, targetName)
        configuration.put(CommonConfigurationKeys.USE_FIR, true)
        configuration.put(CommonConfigurationKeys.USE_LIGHT_TREE, false)
        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, messageCollector)
        configuration.addKotlinSourceRoots(sources.map { it.absolutePath })
        configuration.addJvmClasspathRoots(classpath.toList())

        val files = buildList {
            for (source in sources) {
                source.walkTopDown().filter { it.isFile }.forEach {
                    this += compiler.compile(it.toPath())
                }
            }
        }

        val environment = KotlinCoreEnvironment.createForProduction(
            disposable,
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES,
        )
        val project = environment.project
        val packagePartProvider = environment.createPackagePartProvider(GlobalSearchScope.allScope(project))
        val projectEnvironment = VfsBasedProjectEnvironment(
            project = project,
            localFileSystem = VirtualFileManager.getInstance().getFileSystem(StandardFileSystems.FILE_PROTOCOL),
            getPackagePartProviderFn = { packagePartProvider },
        )

        val messageCollector = environment.messageCollector

        val targetIds = configuration.get(JVMConfigurationKeys.MODULES)?.map(::TargetId)
        val incrementalComponents = configuration.get(JVMConfigurationKeys.INCREMENTAL_COMPILATION_COMPONENTS)

        return object : FrontendContext {
            override val configuration: CompilerConfiguration = configuration
            override val extensionRegistrars: List<FirExtensionRegistrar> = FirExtensionRegistrar.getInstances(project)
            override val incrementalComponents: IncrementalCompilationComponents? = incrementalComponents
            override val messageCollector: MessageCollector = messageCollector
            override val projectEnvironment: VfsBasedProjectEnvironment = projectEnvironment
            override val targetIds: List<TargetId>? = targetIds
        }.runFrontend2(files, createPendingReporter(messageCollector), "", emptyList())
    }

    override fun close() {
        disposable.dispose()
    }
}

private fun FrontendContext.runFrontend2(
    ktFiles: List<KtFile>,
    diagnosticsReporter: BaseDiagnosticsCollector,
    rootModuleName: String,
    friendPaths: List<String>,
): FirResult {
    val performanceManager = configuration.get(CLIConfigurationKeys.PERF_MANAGER)
    performanceManager?.notifyAnalysisStarted()

    val sourceScope =
        projectEnvironment.getSearchScopeByPsiFiles(ktFiles) + projectEnvironment.getSearchScopeForProjectJavaSources()

    var librariesScope = projectEnvironment.getSearchScopeForProjectLibraries()

    val providerAndScopeForIncrementalCompilation = createContextForIncrementalCompilation(
        projectEnvironment,
        incrementalComponents,
        configuration,
        targetIds,
        sourceScope
    )

    providerAndScopeForIncrementalCompilation?.precompiledBinariesFileScope?.let {
        librariesScope -= it
    }
    val libraryList = createLibraryListForJvm(rootModuleName, configuration, friendPaths)
    val sessionsWithSources = prepareJvmSessions(
        ktFiles, configuration, projectEnvironment, Name.special("<$rootModuleName>"),
        extensionRegistrars, librariesScope, libraryList,
        isCommonSource = { it.isCommonSource == true },
        isScript = { it.isScript() },
        fileBelongsToModule = { file, moduleName -> file.hmppModuleName == moduleName },
        createProviderAndScopeForIncrementalCompilation = { providerAndScopeForIncrementalCompilation }
    )

    return FirResult(
        sessionsWithSources.map { (session, sources) ->
            buildResolveAndCheckFirFromKtFiles(session, sources, diagnosticsReporter)
        }
    )
}
