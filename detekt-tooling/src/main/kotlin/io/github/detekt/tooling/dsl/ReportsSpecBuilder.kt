package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.ReportsSpec
import java.nio.file.Path

@ProcessingModelDsl
class ReportsSpecBuilder : Builder<ReportsSpec>, ReportsSpec {

    override var reports: MutableCollection<ReportsSpec.Report> = mutableListOf()

    fun report(init: () -> Pair<String, Path>) {
        reports.add(Report(init()))
    }

    override fun build(): ReportsSpec = ReportsModel(reports)
}

internal data class ReportsModel(override val reports: Collection<ReportsSpec.Report>) : ReportsSpec

internal data class Report(override val type: String, override val path: Path) : ReportsSpec.Report {
    constructor(values: Pair<String, Path>) : this(values.first, values.second)
}
