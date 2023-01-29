package io.github.detekt.parser

import io.github.detekt.psi.BASE_PATH
import io.github.detekt.psi.LINE_SEPARATOR
import io.github.detekt.psi.RELATIVE_PATH
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText

open class KtCompiler(
    protected val environment: KotlinCoreEnvironment = createKotlinCoreEnvironment(printStream = System.err)
) {

    protected val psiFileFactory = KtPsiFactory(environment.project, markGenerated = false)

    fun compile(basePath: Path?, path: Path): KtFile {
        require(path.isRegularFile()) { "Given sub path ($path) should be a regular file!" }
        val content = path.readText()
        return createKtFile(content, basePath, path)
    }

    fun createKtFile(content: String, basePath: Path?, path: Path): KtFile {
        require(path.isRegularFile()) { "Given sub path ($path) should be a regular file!" }

        val normalizedAbsolutePath = path.absolute().normalize()
        val lineSeparator = content.determineLineSeparator()

        val psiFile = psiFileFactory.createPhysicalFile(
            normalizedAbsolutePath.toString(),
            StringUtilRt.convertLineSeparators(content)
        )

        return psiFile.apply {
            putUserData(LINE_SEPARATOR, lineSeparator)
            val normalizedBasePath = basePath?.absolute()?.normalize()
            normalizedBasePath?.relativize(normalizedAbsolutePath)?.let { relativePath ->
                putUserData(BASE_PATH, normalizedBasePath.absolute().toString())
                putUserData(RELATIVE_PATH, relativePath.toString())
            }
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
