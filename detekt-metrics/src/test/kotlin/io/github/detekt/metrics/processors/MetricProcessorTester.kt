package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.psi.KtFile

class MetricProcessorTester(
    private val file: KtFile,
    private val result: Detektion = MetricResults()
) {

    fun <T : Any> test(processor: AbstractProcessor, key: Key<T>): T {
        with(processor) {
            onStart(listOf(file))
            onProcess(file)
            onProcessComplete(file, emptyList())
            onFinish(listOf(file), result)
        }
        return checkNotNull(result.getUserData(key))
    }
}

private class MetricResults : Detektion, UserDataHolderBase() {
    override val issues: List<Issue>
        get() = throw UnsupportedOperationException()
    override val notifications: Collection<Notification>
        get() = throw UnsupportedOperationException()
    override val metrics: MutableList<ProjectMetric> = mutableListOf()

    override fun add(notification: Notification) {
        throw UnsupportedOperationException()
    }

    override fun add(projectMetric: ProjectMetric) {
        metrics.add(projectMetric)
    }
}
