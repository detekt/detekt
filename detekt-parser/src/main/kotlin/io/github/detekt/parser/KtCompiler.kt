package io.github.detekt.parser

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.pom.PomModel
import com.intellij.psi.util.PsiUtilCore
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.cli.common.CliModuleVisibilityManagerImpl
import org.jetbrains.kotlin.load.kotlin.ModuleVisibilityManager
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.isRegularFile

open class KtCompiler(
    protected val project: Project = createDefaultAnalysisAPISession().project,
) {

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

private fun createDefaultAnalysisAPISession() = buildStandaloneAnalysisAPISession {
    registerProjectService(PomModel::class.java, DetektPomModel)

    // Required until BindingContext usage is fully removed
    registerProjectService(ModuleVisibilityManager::class.java, CliModuleVisibilityManagerImpl(true))

    buildKtModuleProvider {
        platform = JvmPlatforms.defaultJvmPlatform
    }
}
