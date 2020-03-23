package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import org.jetbrains.kotlin.psi.KtFile
import java.io.PrintStream

class DetektProgressListener : FileProcessListener {

    private lateinit var outPrinter: PrintStream

    @Deprecated(
        message = "We need to keep the default constructor public because the ServiceLoader use it.",
        level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith(expression = "DetektProgressListener(outPrinter)")
    )
    constructor()

    constructor(outPrinter: PrintStream) {
        this.outPrinter = outPrinter
    }

    @OptIn(UnstableApi::class)
    override fun init(context: SetupContext) {
        this.outPrinter = context.outPrinter
    }

    override fun onStart(files: List<KtFile>) {
        val name = if (files.size == 1) "file" else "files"
        outPrinter.print("Analyzing ${files.size} kotlin $name: ")
    }

    override fun onProcess(file: KtFile) {
        outPrinter.print(".")
    }
}
