package dev.detekt.report.statistics

import dev.detekt.api.ProjectMetric
import dev.detekt.test.invoke
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PackageCountProcessorSpec {
    @Test
    fun counts() {
        val detektion = PackageCountProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(emptyEnum),
            compileContentForTest(emptyInterface),
            compileContentForTest(classWithFields),
            compileContentForTest(commentsClass),
            compileContentForTest(complexClass),
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of packages", 4))
    }

    @Test
    fun twoClassesInSeparatePackage() {
        val detektion = PackageCountProcessor().invoke(
            compileContentForTest(default),
            compileContentForTest(emptyEnum),
        )

        assertThat(detektion.metrics).singleElement()
            .isEqualTo(ProjectMetric("number of packages", 2))
    }
}
