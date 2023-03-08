package io.github.detekt.report.html.freemarker

import freemarker.template.Configuration
import io.github.detekt.metrics.ComplexityReportGenerator
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import java.io.File
import java.io.StringWriter
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class FreemarkerHtmlOutputReport : OutputReport() {

    private val freemarker = createConfiguration()

    override val ending: String = "html"

    override fun render(detektion: Detektion): String {
        return StringWriter().use { writer ->
            val template = freemarker.getTemplate(ROOT_TEMPLATE)
            template.process(createFreemarkerReport(detektion), writer)
            writer.toString()
        }
    }

    private fun createConfiguration(): Configuration =
        Configuration(Configuration.VERSION_2_3_32).apply {
            localizedLookup = false
            setDirectoryForTemplateLoading(File("/Users/ayakovlev/IdeaProjects/detekt-fork/templates").absoluteFile)
        }

    companion object {

        private const val ROOT_TEMPLATE = "report.html.ftl"
    }
}
