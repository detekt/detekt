package io.github.detekt.parser

import io.github.detekt.psi.absolutePath
import io.github.detekt.psi.basePath
import io.github.detekt.psi.lineSeparator
import io.github.detekt.psi.relativePath
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.relativeTo

open class KtCompiler(
    protected val environment: KotlinCoreEnvironment = createKotlinCoreEnvironment(printStream = System.err)
) {

    protected val psiFileFactory = KtPsiFactory(environment.project, markGenerated = false)

    fun compile(basePath: Path, path: Path): KtFile {
        require(path.isRegularFile()) { "Given sub path ($path) should be a regular file!" }
        val content = path.readText()
        return createKtFile(content, basePath, path)
    }

    fun createKtFile(content: String, basePath: Path, path: Path): KtFile {
        require(path.isRegularFile()) { "Given sub path ($path) should be a regular file!" }

        val normalizedAbsolutePath = path.absolute().normalize()
        val lineSeparator = content.determineLineSeparator()

        val psiFile = psiFileFactory.createPhysicalFile(
            normalizedAbsolutePath.name,
            StringUtilRt.convertLineSeparators(content)
        )

        return psiFile.apply {
            this.absolutePath = normalizedAbsolutePath
            this.lineSeparator = lineSeparator
            val normalizedBasePath = basePath.absolute().normalize()
            this.basePath = normalizedBasePath.absolute()
            this.relativePath = normalizedAbsolutePath.relativeTo(normalizedBasePath)
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
