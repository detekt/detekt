package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

class MetricProcessorTester(
    private val file: KtFile,
    private val result: Detektion = MetricResults()
) {

    fun <T : Any> test(processor: AbstractProcessor, key: Key<T>): T {
        with(processor) {
            onStart(listOf(file), BindingContext.EMPTY)
            onProcess(file, BindingContext.EMPTY)
            onProcessComplete(file, emptyList(), BindingContext.EMPTY)
            onFinish(listOf(file), result, BindingContext.EMPTY)
        }
        return checkNotNull(result.getUserData(key))
    }
}

private class MetricResults : Detektion, UserDataHolderBase() {
    override val findings: List<Finding2>
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
