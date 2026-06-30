package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class PropertyCountProcessor : FileProcessListener {
    override val id: String = "PropertyCountProcessor"

    override fun onFinish(files: List<KtFile>, result: Detektion): Detektion {
        val count = files.sumOf { it.collectDescendantsOfType<KtProperty>().size }
        return result.plus(ProjectMetric(numberOfFieldsKey.toString(), count))
    }
}

val numberOfFieldsKey = Key<Int>("number of properties")
