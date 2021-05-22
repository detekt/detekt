package io.gitlab.arturbosch.detekt.core

import io.github.detekt.psi.LINE_SEPARATOR
import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Notification
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.fileClasses.javaFileFacadeFqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Files
import java.nio.file.Path

class KtFileModifier : FileProcessListener {

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        files.filter { it.modificationStamp > 0 }
            .map { it.absolutePath() to it.denormalizeContent() }
            .forEach { (path, content) ->
                result.add(ModificationNotification(path))
                Files.write(path, content.toByteArray())
            }
    }

    private fun KtFile.denormalizeContent(): String {
        val lineSeparator = getUserData(LINE_SEPARATOR)
        require(lineSeparator != null) {
            "No line separator entry for ktFile ${javaFileFacadeFqName.asString()}"
        }
        return StringUtilRt.convertLineSeparators(text, lineSeparator)
    }
}

private class ModificationNotification(path: Path) : Notification {

    override val message: String = "File $path was modified."
    override val level: Notification.Level = Notification.Level.Info
    override fun toString(): String = message
}
