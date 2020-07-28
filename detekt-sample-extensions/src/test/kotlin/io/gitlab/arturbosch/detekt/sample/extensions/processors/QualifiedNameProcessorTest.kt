package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap
import org.jetbrains.kotlin.resolve.BindingContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class QualifiedNameProcessorTest : Spek({

    describe("QualifiedNameProcessor") {

        it("fqNamesOfTestFiles") {
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
})

private val result = object : Detektion {

    override val findings: Map<String, List<Finding>> = mapOf()
    override val notifications: Collection<Notification> = listOf()
    override val metrics: Collection<ProjectMetric> = listOf()

    private var userData = KeyFMap.EMPTY_MAP
    override fun <V> getData(key: Key<V>): V? = userData.get(key)

    override fun <V> addData(key: Key<V>, value: V) {
        userData = userData.plus(key, value)
    }

    override fun add(notification: Notification) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun add(projectMetric: ProjectMetric) {
        throw UnsupportedOperationException("not implemented")
    }
}

const val code = """
    package io.gitlab.arturbosch.detekt.sample

    class Foo {}
    object Bar {}
    interface Bla {}
"""
