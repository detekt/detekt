package dev.detekt.tooling.dsl

import dev.detekt.tooling.api.spec.ReportsSpec
import java.nio.file.Path

@ProcessingModelDsl
class ReportsSpecBuilder : Builder<ReportsSpec> {

    var reports: MutableCollection<ReportsSpec.Report> = mutableListOf()
    var consoleReports: MutableCollection<String> = mutableListOf()

    fun report(init: () -> Pair<String, Path>) {
        reports.add(Report(init()))
    }

    fun consoleReport(consoleReport: String) {
        consoleReports.add(consoleReport)
    }

    override fun build(): ReportsSpec = ReportsModel(reports, consoleReports)
}

private data class ReportsModel(
    override val reports: Collection<ReportsSpec.Report>,
    override val consoleReports: Collection<String>,
) : ReportsSpec

private data class Report(override val type: String, override val path: Path) : ReportsSpec.Report {
    constructor(values: Pair<String, Path>) : this(values.first, values.second)
}
