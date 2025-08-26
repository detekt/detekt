package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.Notification
import dev.detekt.api.ProjectMetric
import dev.detekt.api.RuleInstance
import dev.detekt.metrics.CognitiveComplexity
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class CognitiveComplexityProcessorSpec {

    @Test
    fun `counts the complexity for the whole file`() {
        val file = compileContentForTest(complexClass)

        val value = MetricProcessorTester(file)
            .test(ProjectCognitiveComplexityProcessor(), CognitiveComplexity.KEY)

        assertThat(value).isEqualTo(50)
    }
}

private class MetricProcessorTester(
    private val file: KtFile,
    private val result: Detektion = MetricResults(),
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
    override val rules: List<RuleInstance>
        get() = throw UnsupportedOperationException()
    override val notifications: Collection<Notification>
        get() = throw UnsupportedOperationException()
    override val metrics: MutableList<ProjectMetric> = mutableListOf()

    override fun add(notification: Notification): Unit = throw UnsupportedOperationException()

    override fun add(projectMetric: ProjectMetric) {
        metrics.add(projectMetric)
    }
}
