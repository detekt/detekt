package io.github.detekt.parser

import io.github.detekt.psi.ABSOLUTE_PATH
import io.github.detekt.psi.LINE_SEPARATOR
import io.github.detekt.psi.RELATIVE_PATH
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path

open class KtCompiler(
    protected val environment: KotlinCoreEnvironment = createKotlinCoreEnvironment()
) {

    protected val psiFileFactory: PsiFileFactory = PsiFileFactory.getInstance(environment.project)

    fun compile(basePath: Path, path: Path): KtFile {
        require(Files.isRegularFile(path)) { "Given sub path ($path) should be a regular file!" }
        val content = path.toFile().readText()
        return createKtFile(content, basePath, path)
    }

    fun createKtFile(content: String, basePath: Path, path: Path): KtFile {
        require(Files.isRegularFile(path)) { "Given sub path ($path) should be a regular file!" }

        val relativePath =
            (if (basePath == path) path.fileName
            else basePath.fileName.resolve(basePath.relativize(path))).normalize()
        val absolutePath = path.toAbsolutePath().normalize()
        val lineSeparator = content.determineLineSeparator()

        val psiFile = psiFileFactory.createFileFromText(
            path.fileName.toString(),
            KotlinLanguage.INSTANCE,
            StringUtilRt.convertLineSeparators(content),
            true, true, false,
            LightVirtualFile(path.toString())
        )
        return (psiFile as? KtFile ?: error("kotlin file expected")).apply {
            putUserData(LINE_SEPARATOR, lineSeparator)
            putUserData(RELATIVE_PATH, relativePath.toString())
            putUserData(ABSOLUTE_PATH, absolutePath.toString())
        }
    }
}

internal fun String.determineLineSeparator(): String {
    val i = this.lastIndexOf('\n')
    if (i == -1) {
        return if (this.lastIndexOf('\r') == -1) System.getProperty("line.separator") else "\r"
    }
    return if (i != 0 && this[i - 1] == '\r') "\r\n" else "\n"
}
