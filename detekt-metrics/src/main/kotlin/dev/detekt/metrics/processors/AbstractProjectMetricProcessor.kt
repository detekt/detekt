package dev.detekt.metrics.processors

import dev.detekt.api.Detektion
import dev.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtFile

abstract class AbstractProjectMetricProcessor : AbstractProcessor() {

    val type: String get() = key.toString()

    override fun onFinish(files: List<KtFile>, result: Detektion): Detektion {
        val count = files
            .mapNotNull { it.getUserData(key) }
            .sum()
        return result.plus(ProjectMetric(type, count))
    }
}
