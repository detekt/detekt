package dev.detekt.report.statistics

import com.intellij.openapi.util.Key
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class FunctionCountProcessor : FileProcessListener {
    override val id: String = "FunctionCountProcessor"

    override fun onFinish(files: List<KtFile>, result: Detektion): Detektion {
        val count = files.sumOf { it.collectDescendantsOfType<KtNamedFunction>().size }
        result.add(ProjectMetric(numberOfFunctionsKey.toString(), count))
        return result
    }
}

val numberOfFunctionsKey = Key<Int>("number of functions")
