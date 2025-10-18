package dev.detekt.metrics.processors

import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.testfixtures.TestDetektion
import org.jetbrains.kotlin.psi.KtFile

fun FileProcessListener.invoke(vararg files: KtFile) = invoke(files.toList())

fun FileProcessListener.invoke(files: List<KtFile>): Detektion {
    val result = TestDetektion()
    onStart(files)
    files.forEach {
        onProcess(it)
        onProcessComplete(it, emptyList())
    }
    return onFinish(files, result)
}
