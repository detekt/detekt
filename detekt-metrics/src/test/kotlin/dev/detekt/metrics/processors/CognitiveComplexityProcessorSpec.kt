package dev.detekt.metrics.processors

import com.intellij.openapi.util.Key
import dev.detekt.api.Detektion
import dev.detekt.api.testfixtures.TestDetektion
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
    private val result: Detektion = TestDetektion(),
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
