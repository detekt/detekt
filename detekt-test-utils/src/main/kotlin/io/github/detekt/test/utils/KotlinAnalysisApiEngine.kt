package io.github.detekt.test.utils

import kotlinx.coroutines.CoroutineScope
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaImplementationDetail
import org.jetbrains.kotlin.analysis.api.KaPlatformInterface
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaCompilationResult
import org.jetbrains.kotlin.analysis.api.components.KaCompilerTarget
import org.jetbrains.kotlin.analysis.api.diagnostics.KaDiagnosticWithPsi
import org.jetbrains.kotlin.analysis.api.diagnostics.KaSeverity
import org.jetbrains.kotlin.analysis.api.projectStructure.KaModule
import org.jetbrains.kotlin.analysis.api.projectStructure.contextModule
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSdkModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * The object to use the Kotlin Analysis API for code compilation.
 */
object KotlinAnalysisApiEngine {

    private lateinit var sourceModule: KaModule

    @OptIn(KaImplementationDetail::class, KaPlatformInterface::class, KaExperimentalApi::class)
    private val session = buildStandaloneAnalysisAPISession(unitTestMode = false) {
        buildKtModuleProvider {
            val targetPlatform = JvmPlatforms.defaultJvmPlatform
            platform = targetPlatform

            val jdk = addModule(
                buildKtSdkModule {
                    addBinaryRootsFromJdkHome(Path(System.getProperty("java.home")), true)
                    platform = targetPlatform
                    libraryName = "sdk"
                }
            )

            val stdlib = addModule(
                buildKtLibraryModule {
                    addBinaryRoot(File(CharRange::class.java.protectionDomain.codeSource.location.path).toPath())
                    platform = targetPlatform
                    libraryName = "stdlib"
                }
            )

            val coroutinesCore = addModule(
                buildKtLibraryModule {
                    addBinaryRoot(kotlinxCoroutinesCorePath())
                    platform = targetPlatform
                    libraryName = "coroutines-core"
                }
            )

            sourceModule = addModule(
                buildKtSourceModule {
                    addRegularDependency(jdk)
                    addRegularDependency(stdlib)
                    addRegularDependency(coroutinesCore)
                    platform = targetPlatform
                    moduleName = "source"
                }
            )
        }
    }

    private val factory = KtPsiFactory(session.project)

    private val configuration = CompilerConfiguration()

    @OptIn(KaExperimentalApi::class)
    private val target = KaCompilerTarget.Jvm(false)

    /**
     * Compiles a given code string using Kotlin's Analysis API.
     *
     * @throws IllegalStateException if the given code snippet does not compile
     */
    @OptIn(KaExperimentalApi::class)
    fun compile(@Language("kotlin") code: String) {
        val file = factory.createFile(code).apply {
            contextModule = sourceModule
        }

        analyze(file) {
            val result = compile(file, configuration, target) {
                it.severity != KaSeverity.ERROR
            }

            if (result is KaCompilationResult.Failure) {
                val errors = result.errors.joinToString("\n") {
                    if (it is KaDiagnosticWithPsi<*>) {
                        val lineAndColumn = PsiDiagnosticUtils.offsetToLineAndColumn(
                            it.psi.containingFile.viewProvider.document,
                            it.psi.textOffset
                        )
                        "${it.severity.name} ${it.defaultMessage} (${it.psi.containingFile.name}:${lineAndColumn.line}:${lineAndColumn.column})"
                        "${it.severity.name} ${it.defaultMessage} (${it.psi.containingFile.name}:${lineAndColumn.line}:${lineAndColumn.column})"
                    } else {
                        "${it.severity.name} ${it.defaultMessage}"
                    }
                }

                error(errors)
            }
        }
    }

    private fun kotlinxCoroutinesCorePath(): Path =
        File(CoroutineScope::class.java.protectionDomain.codeSource.location.path).toPath()
}
