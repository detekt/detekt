package dev.detekt.test.utils

import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.LightVirtualFile
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaImplementationDetail
import org.jetbrains.kotlin.analysis.api.KaPlatformInterface
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaCompilationResult
import org.jetbrains.kotlin.analysis.api.components.KaCompilerTarget
import org.jetbrains.kotlin.analysis.api.diagnostics.KaDiagnosticWithPsi
import org.jetbrains.kotlin.analysis.api.diagnostics.KaSeverity
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSdkModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension

/**
 * The object to use the Kotlin Analysis API for code compilation.
 */
@OptIn(KaExperimentalApi::class)
object KotlinAnalysisApiEngine {
    private val targetPlatform = JvmPlatforms.defaultJvmPlatform
    private val configuration = CompilerConfiguration()
    private val target = KaCompilerTarget.Jvm(isTestMode = false, compiledClassHandler = null, debuggerExtension = null)

    /**
     * Compiles a given code string using Kotlin's Analysis API.
     *
     * @throws IllegalStateException if the given code snippet does not compile
     */
    @Suppress("LongMethod")
    fun compile(
        @Language("kotlin") code: String,
        dependencyCodes: List<String> = emptyList(),
        javaSourceRoots: List<Path> = emptyList(),
        jvmClasspathRoots: List<Path> = emptyList(),
        allowCompilationErrors: Boolean = false,
    ): KtFile {
        val disposable = Disposer.newDisposable()

        @OptIn(KaImplementationDetail::class, KaPlatformInterface::class)
        val session = buildStandaloneAnalysisAPISession(disposable) {
            buildKtModuleProvider {
                platform = targetPlatform

                val jdk = buildKtSdkModule {
                    addBinaryRootsFromJdkHome(Path(System.getProperty("java.home")), true)
                    platform = targetPlatform
                    libraryName = "sdk"
                }

                val additionalLibraries = jvmClasspathRoots.distinct().map { path ->
                    buildKtLibraryModule {
                        addBinaryRoot(path)
                        platform = targetPlatform
                        libraryName = path.nameWithoutExtension
                    }
                }

                val vf = LightVirtualFile("dummy.kt", code)

                val depVfs = dependencyCodes.mapIndexed { index, depCode ->
                    LightVirtualFile("dependency_${index + 1}.kt", depCode)
                }

                addModule(
                    buildKtSourceModule {
                        addRegularDependency(jdk)
                        additionalLibraries.forEach(::addRegularDependency)
                        addSourceVirtualFile(vf)
                        addSourceVirtualFiles(depVfs)
                        addSourceRoots(javaSourceRoots)
                        platform = targetPlatform
                        moduleName = "source"
                    }
                )
            }
        }

        try {
            val file = session.modulesWithFiles.values.flatten().single { it.name == "dummy.kt" } as KtFile

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
                        } else {
                            "${it.severity.name} ${it.defaultMessage}"
                        }
                    }

                    if (!allowCompilationErrors) {
                        error(errors)
                    }
                }
            }

            return file
        } finally {
            disposable.dispose()
        }
    }
}
