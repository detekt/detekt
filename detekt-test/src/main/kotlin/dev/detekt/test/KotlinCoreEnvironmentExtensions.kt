package dev.detekt.test

import com.intellij.openapi.vfs.impl.jar.CoreJarFileSystem
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.jetbrains.kotlin.analysis.api.standalone.base.projectStructure.StandaloneProjectFactory
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.cli.jvm.config.jvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.index.JavaRoot
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

fun KotlinEnvironmentContainer.createBindingContext(files: List<KtFile>): BindingContext {
    val vfsFs = CoreJarFileSystem()

    val classpathRoots = configuration.jvmClasspathRoots
        .filter { it.extension == "jar" }
        .mapNotNull { vfsFs.findFileByPath("${it.absolutePath}!/") }
        .map { JavaRoot(it, JavaRoot.RootType.BINARY) }

    val packagePartProvider = StandaloneProjectFactory.createPackagePartsProvider(
        classpathRoots,
        configuration.languageVersionSettings
    )

    return TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
        project,
        files,
        NoScopeRecordCliBindingTrace(project),
        configuration,
        packagePartProvider
    ).bindingContext
}
