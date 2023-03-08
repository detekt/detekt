package io.github.detekt.report.html

import io.github.detekt.report.html.freemarker.FreemarkerHtmlOutputReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

/**
 * Contains rule violations and metrics formatted in a human friendly way, so that it can be inspected in a web browser.
 * See: https://detekt.dev/configurations.html#output-reports
 */
class HtmlOutputReport : OutputReport() {

    override val ending = "html"

    override val name = "HTML report"

    override fun render(detektion: Detektion) =
        FreemarkerHtmlOutputReport().render(detektion)
}

