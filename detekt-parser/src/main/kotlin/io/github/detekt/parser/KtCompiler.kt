package io.github.detekt.parser

import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.pom.PomModel
import com.intellij.psi.util.PsiUtilCore
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.diagnostics.getDefaultMessageWithFactoryName
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.cli.common.CliModuleVisibilityManagerImpl
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.load.kotlin.ModuleVisibilityManager
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.isRegularFile

open class KtCompiler(
    protected val paths: Collection<Path> = emptySet(),
) {
    val analysisSession = buildStandaloneAnalysisAPISession {
        @Suppress("DEPRECATION") // Required until fully transitioned to setting up Kotlin Analysis API session
        buildKtModuleProviderByCompilerConfiguration(CompilerConfiguration())

        buildKtModuleProvider {
            val targetPlatform = JvmPlatforms.defaultJvmPlatform
            platform = targetPlatform

            addModule(
                buildKtSourceModule {
                    addSourceRoots(paths)
                    platform = targetPlatform
                    moduleName = "source"
                }
            )
        }

        registerProjectService(PomModel::class.java, DetektPomModel)
        registerProjectService(ModuleVisibilityManager::class.java, CliModuleVisibilityManagerImpl(true))
    }

    protected val project = analysisSession.project

    init {
        val files = analysisSession.modulesWithFiles.entries.single().value.map { it as KtFile }

        files.forEach { ktFile ->
            analyze(ktFile) {
                val diagnostics = ktFile
                    .collectDiagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)

                println(diagnostics.joinToString("\n") {
                    "${ktFile.viewProvider.virtualFile.toNioPath()}:${it.getDefaultMessageWithFactoryName()}"
                })
            }
        }
    }

    fun compile(path: Path): KtFile {
        require(path.isRegularFile()) { "Given path '$path' should be a regular file!" }

        val virtualFile = requireNotNull(VirtualFileManager.getInstance().findFileByNioPath(path)) {
            "$path cannot be retrieved as a VirtualFile"
        }

        return requireNotNull(PsiUtilCore.getPsiFile(project, virtualFile) as? KtFile) {
            "$path is not a Kotlin file"
        }
    }
}
