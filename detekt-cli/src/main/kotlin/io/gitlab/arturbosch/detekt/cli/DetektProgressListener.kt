package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.api.UnstableApi
import org.jetbrains.kotlin.psi.KtFile
import java.io.PrintStream

class DetektProgressListener : FileProcessListener {

    private var outPrinter: PrintStream by SingleAssign()

    @OptIn(UnstableApi::class)
    override fun init(context: SetupContext) {
        this.outPrinter = context.outPrinter
    }

    override fun onProcess(file: KtFile) {
        outPrinter.print(".")
    }

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        val middlePart = if (files.size == 1) "file was" else "files were"
        outPrinter.println("\n\n${files.size} kotlin $middlePart analyzed.")
    }
}
