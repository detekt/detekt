package dev.detekt.metrics.processors

import dev.detekt.api.ProjectMetric
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClassCountProcessorSpec {
    @Test
    fun counts() {
        val detektion = ClassCountProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface),
            compileContentForTest(classWithFields),
            compileContentForTest(commentsClass),
            compileContentForTest(complexClass),
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of classes", 7))
    }

    @Test
    fun twoClassesInSeparateFile() {
        val detektion = ClassCountProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(classWithFields)
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of classes", 2))
    }

    @Test
    fun oneClassWithOneNestedClass() {
        val detektion = ClassCountProcessor().invoke(
            compileContentForTest(complexClass)
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of classes", 2))
    }

    @Test
    fun testEnumAndInterface() {
        val detektion = ClassCountProcessor().invoke(
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface)
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of classes", 2))
    }
}
