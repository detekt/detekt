package io.github.detekt.parser

import org.jetbrains.kotlin.KtVirtualFileSourceFile
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.GroupedKtSources
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.*
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.VfsBasedProjectEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.pipeline.ModuleCompilerInput
import org.jetbrains.kotlin.cli.jvm.compiler.pipeline.compileModuleToAnalyzedFir
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.vfs.StandardFileSystems
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFileManager
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.diagnostics.DiagnosticReporterFactory
import org.jetbrains.kotlin.fir.pipeline.FirResult
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.modules.TargetId
import org.jetbrains.kotlin.platform.CommonPlatforms
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
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
        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, messageCollector)
        configuration.addKotlinSourceRoots(sources.map { it.absolutePath })
        configuration.addJvmClasspathRoots(classpath.toList())

        val environment = KotlinCoreEnvironment.createForProduction(
            disposable,
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES,
        )
        val project = environment.project

        val localFileSystem = VirtualFileManager.getInstance().getFileSystem(
            StandardFileSystems.FILE_PROTOCOL,
        )
        val files = buildList {
            for (source in sources) {
                source.walkTopDown().filter { it.isFile }.forEach {
                    this += localFileSystem.findFileByPath(it.absolutePath)!!
                }
            }
        }

        val sourceFiles = files.mapTo(mutableSetOf(), ::KtVirtualFileSourceFile)
        val input = ModuleCompilerInput(
            targetId = TargetId(JvmProtoBufUtil.DEFAULT_MODULE_NAME, targetName),
            groupedSources = GroupedKtSources(
                platformSources = sourceFiles,
                commonSources = emptyList(),
                sourcesByModuleName = mapOf(JvmProtoBufUtil.DEFAULT_MODULE_NAME to sourceFiles),
            ),
            commonPlatform = CommonPlatforms.defaultCommonPlatform,
            platform = JvmPlatforms.unspecifiedJvmPlatform,
            configuration = configuration,
        )

        val reporter = DiagnosticReporterFactory.createReporter()

        val globalScope = GlobalSearchScope.allScope(project)
        val packagePartProvider = environment.createPackagePartProvider(globalScope)
        val projectEnvironment = VfsBasedProjectEnvironment(
            project = project,
            localFileSystem = localFileSystem,
            getPackagePartProviderFn = { packagePartProvider },
        )

        return compileModuleToAnalyzedFir(
            input = input,
            projectEnvironment = projectEnvironment,
            previousStepsSymbolProviders = emptyList(),
            incrementalExcludesScope = null,
            diagnosticsReporter = reporter,
        )
    }

    override fun close() {
        disposable.dispose()
    }
}
