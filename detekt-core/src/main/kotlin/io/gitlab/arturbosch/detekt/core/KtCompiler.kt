package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.internal.ABSOLUTE_PATH
import io.gitlab.arturbosch.detekt.api.internal.RELATIVE_PATH
import io.gitlab.arturbosch.detekt.api.psiProject
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
open class KtCompiler {

    protected val psiFileFactory: PsiFileFactory = PsiFileFactory.getInstance(psiProject)

    fun compile(root: Path, subPath: Path): KtFile {
        require(subPath.isFile()) { "Given sub path should be a regular file!" }
        val relativePath =
                (if (root == subPath) subPath.fileName
                else root.fileName.resolve(root.relativize(subPath))).normalize()
        val absolutePath =
                (if (root == subPath) subPath
                else subPath).toAbsolutePath().normalize()
        val content = subPath.toFile().readText()
        val lineSeparator = content.determineLineSeparator()
        val normalizedContent = StringUtilRt.convertLineSeparators(content)
        val ktFile = createKtFile(normalizedContent, absolutePath)

        return ktFile.apply {
            putUserData(LINE_SEPARATOR, lineSeparator)
            putUserData(RELATIVE_PATH, relativePath.toString())
            putUserData(ABSOLUTE_PATH, absolutePath.toString())
        }
    }

    private fun createKtFile(content: String, path: Path): KtFile {
        val psiFile = psiFileFactory.createFileFromText(
                path.fileName.toString(),
                KotlinLanguage.INSTANCE,
                StringUtilRt.convertLineSeparators(content),
                true, true, false,
                LightVirtualFile(path.toString()))
        return psiFile as? KtFile ?: throw IllegalStateException("kotlin file expected")
    }

    private fun String.determineLineSeparator(): String {
        val i = this.lastIndexOf('\n')
        if (i == -1) {
            return if (this.lastIndexOf('\r') == -1) System.getProperty("line.separator") else "\r"
        }
        return if (i != 0 && this[i] == '\r') "\r\n" else "\n"
    }
}
