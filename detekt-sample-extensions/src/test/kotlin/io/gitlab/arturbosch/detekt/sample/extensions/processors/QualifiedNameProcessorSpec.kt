package io.gitlab.arturbosch.detekt.sample.extensions.processors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test

class QualifiedNameProcessorSpec {

    @Test
    fun fqNamesOfTestFiles() {
        val ktFile = compileContentForTest(code)
        val processor = QualifiedNameProcessor()
        processor.onProcess(ktFile, BindingContext.EMPTY)
        processor.onFinish(listOf(ktFile), result, BindingContext.EMPTY)

        val data = result.getUserData(fqNamesKey)
        assertThat(data).contains(
            "io.gitlab.arturbosch.detekt.sample.Foo",
            "io.gitlab.arturbosch.detekt.sample.Bar",
            "io.gitlab.arturbosch.detekt.sample.Bla"
        )
    }
}

private val result = object : Detektion, UserDataHolderBase() {

    override val findings: List<Finding2> = emptyList()
    override val notifications: Collection<Notification> = emptyList()
    override val metrics: Collection<ProjectMetric> = emptyList()

    override fun add(notification: Notification) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun add(projectMetric: ProjectMetric) {
        throw UnsupportedOperationException("not implemented")
    }
}

private val code = """
    package io.gitlab.arturbosch.detekt.sample

    class Foo {}
    object Bar {}
    interface Bla {}
""".trimIndent()
