package dev.detekt.test.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtilRt
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.pom.PomModel
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.util.PsiUtilCore
import dev.detekt.parser.DetektPomModel
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 */
internal object KtTestCompiler {
    val project: Project = createDefaultAnalysisAPISession().project
    private val psiFileFactory = KtPsiFactory(project, markGenerated = false)

    fun createKtFile(@Language("kotlin") content: String, path: Path): KtFile =
        psiFileFactory.createPhysicalFile(path.name, StringUtilRt.convertLineSeparators(content))

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
    registerProjectService(TreeAspect::class.java)
    registerProjectService(PomModel::class.java, DetektPomModel(project))

    buildKtModuleProvider {
        platform = JvmPlatforms.defaultJvmPlatform
    }
}
