package dev.detekt.test

import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.Issue
import dev.detekt.api.testfixtures.TestDetektion
import org.jetbrains.kotlin.psi.KtFile

fun FileProcessListener.invoke(
    vararg files: KtFile,
    detektion: Detektion = TestDetektion(),
    issues: List<Issue> = emptyList(),
) = invoke(files.toList(), detektion, issues)

fun FileProcessListener.invoke(
    files: List<KtFile>,
    detektion: Detektion = TestDetektion(),
    issues: List<Issue> = emptyList(),
): Detektion {
    onStart(files)
    files.forEach {
        onProcess(it)
        onProcessComplete(it, issues)
    }
    return onFinish(files, detektion)
}
