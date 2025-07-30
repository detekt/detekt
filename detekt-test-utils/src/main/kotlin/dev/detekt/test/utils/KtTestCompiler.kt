package dev.detekt.test.utils

import com.intellij.openapi.util.text.StringUtilRt
import dev.detekt.parser.KtCompiler
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import java.nio.file.Path
import kotlin.io.path.name

/**
 * Test compiler extends kt compiler and adds ability to compile from text content.
 */
internal object KtTestCompiler : KtCompiler() {

    private val psiFileFactory = KtPsiFactory(project, markGenerated = false)

    fun createKtFile(@Language("kotlin") content: String, path: Path): KtFile =
        psiFileFactory.createPhysicalFile(path.name, StringUtilRt.convertLineSeparators(content))
}
