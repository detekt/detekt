package io.gitlab.arturbosch.detekt.sample.extensions.processors

import com.intellij.openapi.util.Key
import com.intellij.util.keyFMap.KeyFMap
import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.Notification
import dev.detekt.api.ProjectMetric
import dev.detekt.api.RuleInstance
import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QualifiedNameProcessorSpec {

    @Test
    fun fqNamesOfTestFiles() {
        val ktFile = compileContentForTest(code)
        val processor = QualifiedNameProcessor()
        processor.onProcess(ktFile)
        processor.onFinish(listOf(ktFile), result)

        val data = result.getUserData(fqNamesKey)
        assertThat(data).contains(
            "io.gitlab.arturbosch.detekt.sample.Foo",
            "io.gitlab.arturbosch.detekt.sample.Bar",
            "io.gitlab.arturbosch.detekt.sample.Bla"
        )
    }
}

private val result = object : Detektion {

    override val issues: List<Issue> = emptyList()
    override val rules: List<RuleInstance> = emptyList()
    override val notifications: Collection<Notification> = emptyList()
    override val metrics: Collection<ProjectMetric> = emptyList()

    private var userData = KeyFMap.EMPTY_MAP
    override fun <V> getUserData(key: Key<V>): V? = userData[key]
    override fun <T : Any?> putUserData(key: Key<T?>, value: T?) {
        userData = userData.plus(key, requireNotNull(value))
    }

    override fun add(notification: Notification): Unit = throw UnsupportedOperationException("not implemented")

    override fun add(projectMetric: ProjectMetric): Unit = throw UnsupportedOperationException("not implemented")
}

private val code = """
    package io.gitlab.arturbosch.detekt.sample

    class Foo {}
    object Bar {}
    interface Bla {}
""".trimIndent()
