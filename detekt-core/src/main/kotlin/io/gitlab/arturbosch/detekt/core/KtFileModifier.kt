package io.gitlab.arturbosch.detekt.core

import com.intellij.openapi.util.text.StringUtilRt
import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.modifiedText
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import kotlin.io.path.writeText

class KtFileModifier : FileProcessListener {

    override val id: String = "KtFileModifier"

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        files.filter { it.modifiedText != null }
            .forEach { ktFile ->
                val path = ktFile.absolutePath()
                result.add(ModificationNotification(path))
                path.writeText(ktFile.unnormalizeContent())
                // reset modification text after writing as the PsiFile may be reused in tests or an IDE session
                ktFile.modifiedText = null
            }
    }

    private fun KtFile.unnormalizeContent(): String =
        StringUtilRt.convertLineSeparators(
            checkNotNull(modifiedText),
            checkNotNull(virtualFile.detectedLineSeparator) {
                "Line separator was not automatically detected. This is unexpected."
            }
        )
}

private class ModificationNotification(path: Path) : Notification {

    override val message: String = "File $path was modified."
    override val level: Notification.Level = Notification.Level.Info
    override fun toString(): String = message
}
