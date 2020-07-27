package io.github.detekt.metrics.processors

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleSetId
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap
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
            onProcessComplete(file, emptyMap(), BindingContext.EMPTY)
            onFinish(listOf(file), result, BindingContext.EMPTY)
        }
        return checkNotNull(result.getData(key))
    }
}

private class MetricResults : Detektion {
    override val findings: Map<RuleSetId, List<Finding>>
        get() = throw UnsupportedOperationException()
    override val notifications: Collection<Notification>
        get() = throw UnsupportedOperationException()
    override val metrics: MutableList<ProjectMetric> = mutableListOf()

    private var data = KeyFMap.EMPTY_MAP

    override fun <V> getData(key: Key<V>): V? = data.get(key)

    override fun <V> addData(key: Key<V>, value: V) {
        data = data.plus(key, value)
    }

    override fun add(notification: Notification) {
        throw UnsupportedOperationException()
    }

    override fun add(projectMetric: ProjectMetric) {
        metrics.add(projectMetric)
    }
}
