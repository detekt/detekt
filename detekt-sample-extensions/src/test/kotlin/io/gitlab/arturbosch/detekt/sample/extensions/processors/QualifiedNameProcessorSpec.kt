package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test

class QualifiedNameProcessorSpec {

    @Test
    fun fqNamesOfTestFiles() {
        val ktFile = compileContentForTest(code)
        val processor = QualifiedNameProcessor()
        processor.onProcess(ktFile, BindingContext.EMPTY)
        processor.onFinish(listOf(ktFile), result, BindingContext.EMPTY)

        val data = result.getData(fqNamesKey)
        assertThat(data).contains(
            "io.gitlab.arturbosch.detekt.sample.Foo",
            "io.gitlab.arturbosch.detekt.sample.Bar",
            "io.gitlab.arturbosch.detekt.sample.Bla"
        )
    }
}

private val result = object : Detektion {

    override val findings: Map<String, List<Finding>> = emptyMap()
    override val notifications: Collection<Notification> = emptyList()
    override val metrics: Collection<ProjectMetric> = emptyList()

    private var userData = KeyFMap.EMPTY_MAP
    override fun <V> getData(key: Key<V>): V? = userData[key]

    override fun <V> addData(key: Key<V>, value: V) {
        userData = userData.plus(key, requireNotNull(value))
    }

    override fun add(notification: Notification) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun add(projectMetric: ProjectMetric) {
        throw UnsupportedOperationException("not implemented")
    }
}

private const val code = """
    package io.gitlab.arturbosch.detekt.sample

    class Foo {}
    object Bar {}
    interface Bla {}
"""
