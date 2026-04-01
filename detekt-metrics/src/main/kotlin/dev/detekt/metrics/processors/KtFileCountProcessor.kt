package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.Detektion
import dev.detekt.api.FileProcessListener
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtFile

class KtFileCountProcessor : FileProcessListener {
    override val id: String = "KtFileCountProcessor"

    override fun onFinish(files: List<KtFile>, result: Detektion): Detektion {
        result.add(ProjectMetric(numberOfFilesKey.toString(), files.count()))
        return result
    }
}

val numberOfFilesKey = Key<Int>("number of kt files")
