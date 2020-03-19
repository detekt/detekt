package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import org.jetbrains.kotlin.psi.KtFile

class DetektProgressListener : FileProcessListener {

    override fun onProcess(file: KtFile) {
        print(".")
    }

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        val middlePart = if (files.size == 1) "file was" else "files were"
        println("\n\n${files.size} kotlin $middlePart analyzed.")
    }
}
