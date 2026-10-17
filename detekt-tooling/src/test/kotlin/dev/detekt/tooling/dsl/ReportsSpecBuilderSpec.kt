package dev.detekt.tooling.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class ReportsSpecBuilderSpec {
    @Test
    fun `builds reports with their types and paths`() {
        val checkstyle = "checkstyle" to Path("build/reports/detekt.xml")
        val custom = "custom" to Path("build/reports/detekt.custom")

        val actualReports = ReportsSpecBuilder().apply {
            report { checkstyle }
            report { custom }
        }.build().reports.map { it.type to it.path }

        assertThat(actualReports).containsExactlyInAnyOrder(checkstyle, custom)
    }
}
