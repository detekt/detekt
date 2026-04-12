package dev.detekt.test.utils

import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.LightVirtualFile
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSdkModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.lang.AutoCloseable
import java.nio.file.Path
import kotlin.io.path.Path

private val shouldCompileTestSnippets by lazy(LazyThreadSafetyMode.NONE) {
    System.getProperty("compile-test-snippets", "false")!!.toBoolean()
}

/**
 * The object to use the Kotlin Analysis API for code compilation.
 */
class KotlinAnalysisApiEngine : AutoCloseable {
    private val targetPlatform = JvmPlatforms.defaultJvmPlatform
    private val disposable = Disposer.newDisposable()

    /**
     * Compiles a given code string using Kotlin's Analysis API.
     *
     * @throws IllegalStateException if the given code snippet does not compile
     */
    fun compile(
        @Language("kotlin") code: String,
        dependencyCodes: List<String> = emptyList(),
        javaSourceRoots: List<Path> = emptyList(),
        jvmClasspathRoots: List<Path> =
            listOf(File(CharRange::class.java.protectionDomain.codeSource.location.path).toPath()),
        allowCompilationErrors: Boolean = shouldCompileTestSnippets,
    ): KtFile {
        val session = buildStandaloneAnalysisAPISession(disposable) {
            buildKtModuleProvider {
                platform = targetPlatform

                val jdk = buildKtSdkModule {
                    addBinaryRootsFromJdkHome(Path(System.getProperty("java.home")), true)
                    platform = targetPlatform
                    libraryName = "sdk"
                }

                val additionalLibraries = buildKtLibraryModule {
                    addBinaryRoots(jvmClasspathRoots.distinct())
                    platform = targetPlatform
                    libraryName = "classpath"
                }

                val vf = LightVirtualFile("dummy.kt", code)

                val depVfs = dependencyCodes.mapIndexed { index, depCode ->
                    LightVirtualFile("dependency_${index + 1}.kt", depCode)
                }

                addModule(
                    buildKtSourceModule {
                        addRegularDependency(jdk)
                        addRegularDependency(additionalLibraries)
                        addSourceVirtualFile(vf)
                        addSourceVirtualFiles(depVfs)
                        addSourceRoots(javaSourceRoots)
                        platform = targetPlatform
                        moduleName = "source"
                    }
                )
            }
        }

        return (session.modulesWithFiles.values.flatten().single { it.name == "dummy.kt" } as KtFile).also {
            if (!allowCompilationErrors) it.checkNoCompilationErrors()
        }
    }

    override fun close() {
        Disposer.dispose(disposable)
    }
}
