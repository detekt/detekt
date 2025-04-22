package io.github.detekt.parser

import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.pom.PomModel
import com.intellij.psi.util.PsiUtilCore
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.diagnostics.getDefaultMessageWithFactoryName
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSdkModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.cli.common.CliModuleVisibilityManagerImpl
import org.jetbrains.kotlin.cli.jvm.config.jvmClasspathRoots
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.friendPaths
import org.jetbrains.kotlin.config.jdkHome
import org.jetbrains.kotlin.config.jvmTarget
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.load.kotlin.ModuleVisibilityManager
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.isRegularFile

open class KtCompiler(
    protected val paths: Collection<Path> = emptySet(),
    private val compilerConfiguration: CompilerConfiguration = CompilerConfiguration(),
) {
    val analysisSession = buildStandaloneAnalysisAPISession(unitTestMode = true) {
        registerProjectService(PomModel::class.java, DetektPomModel)
        registerProjectService(ModuleVisibilityManager::class.java, CliModuleVisibilityManagerImpl(true))

        buildKtModuleProvider {
            val targetPlatform =
                JvmPlatforms.jvmPlatformByTargetVersion(compilerConfiguration.jvmTarget ?: JvmTarget.DEFAULT)
            platform = targetPlatform

            val jdk = compilerConfiguration.jdkHome?.let { jdkHome ->
                buildKtSdkModule {
                    addBinaryRootsFromJdkHome(jdkHome.toPath(), isJre = false)
                    platform = targetPlatform
                    libraryName = "jdk"
                }
            }

            val friendDependencyModules = compilerConfiguration.friendPaths.filter { it.contains("classes") }.map { friendPath ->
                buildKtLibraryModule {
                    platform = targetPlatform
                    addBinaryRoot(Path(friendPath))
                    libraryName = UUID.randomUUID().toString()
                }
            }

            val regularDependencyModules = compilerConfiguration.jvmClasspathRoots.map { regularDependencyFile ->
                buildKtLibraryModule {
                    platform = targetPlatform
                    addBinaryRoot(regularDependencyFile.toPath())
                    libraryName = UUID.randomUUID().toString()
                }
            }

            val sourceModule = buildKtSourceModule {
//                addSourceRoots(compilerConfiguration.kotlinSourceRoots.map { Path(it.path) })
                addSourceRoots(paths)
                platform = targetPlatform
                moduleName = "source"

                jdk?.let { addRegularDependency(it) }
                friendDependencyModules.forEach { addFriendDependency(it) }
                regularDependencyModules.forEach { addRegularDependency(it) }

                languageVersionSettings = compilerConfiguration.languageVersionSettings
            }

            addModule(sourceModule)
        }
    }

    protected val project = analysisSession.project
    val files = analysisSession.modulesWithFiles.flatMap { it.value }.map { it as KtFile }

    init {
        files.filter { it.virtualFilePath.contains("SignaturesSpec.kt") }.forEach { ktFile ->
            analyze(ktFile) {
                val diagnostics = ktFile
                    .collectDiagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)

                if (diagnostics.isNotEmpty()) {
                    print(diagnostics.joinToString("\n", postfix = "\n") {
                        buildString {
                            append(ktFile.viewProvider.virtualFile.toNioPath())
                            append(":")
                            append(PsiDiagnosticUtils.atLocation(it.psi))
                            append(":")
                            append(it.getDefaultMessageWithFactoryName())
                        }
//                        "${ktFile.viewProvider.virtualFile.toNioPath()}:${it.getDefaultMessageWithFactoryName()}"
                    })
                }
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
