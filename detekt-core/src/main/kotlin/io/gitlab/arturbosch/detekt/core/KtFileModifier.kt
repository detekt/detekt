package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.FormattingInfo
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.fileClasses.javaFileFacadeFqName
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class KtFileModifier(private val project: Path) {

    fun saveModifiedFiles(ktFiles: List<KtFile>, callback: (FormattingInfo) -> Unit) {
        ktFiles.filter { it.modificationStamp > 0 }
                .map { it.absolutePath() to it.unnormalizeContent() }
                .filter { it.first != null }
                .map { project.resolve(it.first) to it.second }
                .forEach {
                    callback.invoke(FormattingInfo(it.first, it.second))
                }
    }

    private fun KtFile.unnormalizeContent(): String {
        val lineSeparator = getUserData(LINE_SEPARATOR)
        require(lineSeparator != null) {
            "No line separator entry for ktFile ${javaFileFacadeFqName.asString()}"
        }
        return StringUtilRt.convertLineSeparators(text, lineSeparator)
    }
}
