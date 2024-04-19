package io.gitlab.arturbosch.detekt.core

import io.github.detekt.psi.absolutePath
import io.github.detekt.psi.lineSeparator
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.modifiedText
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.writeText

class KtFileModifier : FileProcessListener {

    override val id: String = "KtFileModifier"

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        files.filter { it.modifiedText != null }
            .map { it.absolutePath() to it.unnormalizeContent() }
            .forEach { (path, content) ->
                result.add(ModificationNotification(path))
                path.writeText(content)
            }
    }

    private fun KtFile.unnormalizeContent(): String =
        StringUtilRt.convertLineSeparators(
            checkNotNull(modifiedText),
            lineSeparator
        )
}

private class ModificationNotification(path: Path) : Notification {

    override val message: String = "File $path was modified."
    override val level: Notification.Level = Notification.Level.Info
    override fun toString(): String = message
}
