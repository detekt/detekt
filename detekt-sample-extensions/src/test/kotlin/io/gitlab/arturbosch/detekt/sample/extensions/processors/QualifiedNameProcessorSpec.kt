package io.gitlab.arturbosch.detekt.sample.extensions.processors

import dev.detekt.api.Detektion
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class QualifiedNameProcessorSpec {

    @Test
    fun fqNamesOfTestFiles() {
        val ktFile = compileContentForTest(code)
        val processor = QualifiedNameProcessor()
        processor.onProcess(ktFile)
        val result = processor.onFinish(listOf(ktFile), Detektion(emptyList(), emptyList()))

        val data = result.userData[fqNamesKey.toString()] as Set<*>?
        assertThat(data).contains(
            "io.gitlab.arturbosch.detekt.sample.Foo",
            "io.gitlab.arturbosch.detekt.sample.Bar",
            "io.gitlab.arturbosch.detekt.sample.Bla"
        )
    }
}

private val code = """
    package io.gitlab.arturbosch.detekt.sample

    class Foo {}
    object Bar {}
    interface Bla {}
""".trimIndent()
