package dev.detekt.test.utils

import com.google.devtools.ksp.standalone.buildKspLibraryModule
import com.google.devtools.ksp.standalone.buildKspSdkModule
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
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
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * The object to use the Kotlin Analysis API for code compilation.
 */
@OptIn(KaExperimentalApi::class)
object KotlinAnalysisApiEngine {

    private lateinit var sourceModule: KaModule
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
        allowCompilationErrors: Boolean = false,
    ): KtFile {
        val disposable = Disposer.newDisposable()

        @OptIn(KaImplementationDetail::class, KaPlatformInterface::class)
        val session = buildStandaloneAnalysisAPISession(disposable) {
            buildKtModuleProvider {
                platform = targetPlatform

                val jdk = addModule(
                    buildKspSdkModule {
                        addBinaryRootsFromJdkHome(Path(System.getProperty("java.home")), true)
                        platform = targetPlatform
                        libraryName = "sdk"
                    }
                )

                val stdlib = addModule(
                    buildKspLibraryModule {
                        addBinaryRoot(File(CharRange::class.java.protectionDomain.codeSource.location.path).toPath())
                        platform = targetPlatform
                        libraryName = "stdlib"
                    }
                )

                val coroutinesCore = addModule(
                    buildKspLibraryModule {
                        addBinaryRoot(kotlinxCoroutinesCorePath())
                        platform = targetPlatform
                        libraryName = "coroutines-core"
                    }
                )

                val coroutinesTest = addModule(
                    buildKspLibraryModule {
                        addBinaryRoot(kotlinxCoroutinesTestPath())
                        platform = targetPlatform
                        libraryName = "coroutines-test"
                    }
                )

                val vf = LightVirtualFile("dummy.kt", code)

                val depVfs = dependencyCodes.mapIndexed { index, depCode ->
                    LightVirtualFile("dependency_${index + 1}.kt", depCode)
                }

                sourceModule = addModule(
                    buildKtSourceModule {
                        addRegularDependency(jdk)
                        addRegularDependency(stdlib)
                        addRegularDependency(coroutinesCore)
                        addRegularDependency(coroutinesTest)
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

    private fun kotlinxCoroutinesCorePath(): Path =
        File(CoroutineScope::class.java.protectionDomain.codeSource.location.path).toPath()

    private fun kotlinxCoroutinesTestPath(): Path =
        File(TestScope::class.java.protectionDomain.codeSource.location.path).toPath()
}
