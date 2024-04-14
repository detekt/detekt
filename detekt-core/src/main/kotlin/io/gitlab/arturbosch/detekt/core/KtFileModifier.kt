package io.gitlab.arturbosch.detekt.core

import io.github.detekt.psi.absolutePath
import io.github.detekt.psi.lineSeparator
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Notification
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Path
import kotlin.io.path.writeText

class KtFileModifier : FileProcessListener {

    override val id: String = "KtFileModifier"

    override fun onFinish(files: List<KtFile>, result: Detektion, bindingContext: BindingContext) {
        files.filter { it.modificationStamp > 0 }
            .map { it.absolutePath() to it.unnormalizeContent() }
            .forEach { (path, content) ->
                result.add(ModificationNotification(path))
                path.writeText(content)
            }
    }

    private fun KtFile.unnormalizeContent(): String {
        return StringUtilRt.convertLineSeparators(text, lineSeparator)
    }
}

private class ModificationNotification(path: Path) : Notification {

    override val message: String = "File $path was modified."
    override val level: Notification.Level = Notification.Level.Info
    override fun toString(): String = message
}
