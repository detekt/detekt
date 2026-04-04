package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

class ClassCountProcessor : FileProcessListener {
    override val id: String = "ClassCountProcessor"

    override fun onFinish(files: List<KtFile>, result: Detektion): Detektion {
        val count = files.sumOf { it.collectDescendantsOfType<KtClass>().size }
        result.add(ProjectMetric(numberOfClassesKey.toString(), count))
        return result
    }
}

val numberOfClassesKey = Key<Int>("number of classes")
