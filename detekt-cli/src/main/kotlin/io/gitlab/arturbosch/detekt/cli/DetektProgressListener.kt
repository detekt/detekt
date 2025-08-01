package io.gitlab.arturbosch.detekt.cli

import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.SetupContext
import org.jetbrains.kotlin.psi.KtFile

class DetektProgressListener : FileProcessListener {

    private lateinit var outPrinter: Appendable

    override val id: String = "DetektProgressListener"

    override fun init(context: SetupContext) {
        this.outPrinter = context.outputChannel
    }

    override fun onProcess(file: KtFile) {
        outPrinter.append('.')
    }

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        val middlePart = if (files.size == 1) "file was" else "files were"
        outPrinter.appendLine("\n\n${files.size} kotlin $middlePart analyzed.")
    }
}
