package io.github.detekt.parser

import io.github.detekt.psi.absolutePath
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.psi.util.PsiUtilCore
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

open class KtCompiler(
    protected val environment: KotlinCoreEnvironment = createKotlinCoreEnvironment(printStream = System.err)
) {

    protected val psiFileFactory = KtPsiFactory(environment.project, markGenerated = false)

    fun compile(path: Path): KtFile {
        require(path.isRegularFile()) { "Given path '$path' should be a regular file!" }

        val virtualFile = requireNotNull(environment.findLocalFile(path.toString()))
        return requireNotNull(PsiUtilCore.getPsiFile(environment.project, virtualFile) as? KtFile) {
            "$path is not a Kotlin file"
        }
    }

    fun createKtFile(@Language("kotlin") content: String, path: Path): KtFile {
        val psiFile = psiFileFactory.createPhysicalFile(path.name, StringUtilRt.convertLineSeparators(content))

        return psiFile.apply {
            val normalizedAbsolutePath = path.absolute().normalize()
            this.absolutePath = normalizedAbsolutePath
            virtualFile.detectedLineSeparator = content.determineLineSeparator()
        }
    }
}

internal fun String.determineLineSeparator(): String {
    val i = this.lastIndexOf('\n')
    if (i == -1) {
        return if (this.lastIndexOf('\r') == -1) System.lineSeparator() else "\r"
    }
    return if (i != 0 && this[i - 1] == '\r') "\r\n" else "\n"
}
