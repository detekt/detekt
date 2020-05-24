package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.psi.KtFile

abstract class AbstractProjectMetricProcessor : AbstractProcessor() {

    val type: String get() = key.toString()

    override fun onFinish(files: List<KtFile>, result: Detektion) {
        val count = files
                .mapNotNull { it.getUserData(key) }
                .sum()
        result.add(ProjectMetric(type, count))
    }
}
