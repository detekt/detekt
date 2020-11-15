package io.github.detekt.report.sarif

import io.github.detekt.sarif4j.ArtifactLocation
import io.github.detekt.sarif4j.Location
import io.github.detekt.sarif4j.Message
import io.github.detekt.sarif4j.MoshiSarifJsonWriter
import io.github.detekt.sarif4j.PhysicalLocation
import io.github.detekt.sarif4j.Region
import io.github.detekt.sarif4j.Result
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.api.UnstableApi
import java.net.URI

class SarifOutputReport : OutputReport() {

    override val ending: String = "sarif"
    override val id: String = "sarif"
    override val name = "SARIF: a standard format for the output of static analysis tools"

    private var config: Config by SingleAssign()

    @OptIn(UnstableApi::class)
    override fun init(context: SetupContext) {
        this.config = context.config
    }

    override fun render(detektion: Detektion): String {
        val report = sarif {
            withDetektRun(config) {
                for ((ruleSetId, issues) in detektion.findings) {
                    for (issue in issues) {
                        results.add(issue.toIssue(ruleSetId))
                    }
                }
            }
        }
        return MoshiSarifJsonWriter().toJson(report)
    }
}

fun Finding.toIssue(ruleSetId: RuleSetId): SarifIssue = result {
    ruleId = "detekt.$ruleSetId.$id"
    level = Result.Level.WARNING
    hostedViewerUri = URI.create(entity.location.file)
    for (location in listOf(location) + references.map { it.location }) {
        locations.add(Location().apply {
            physicalLocation = PhysicalLocation().apply {
                region = Region().apply {
                    startLine = location.source.line
                    startColumn = location.source.column
                }
                artifactLocation = ArtifactLocation().apply {
                    uri = URI.create(location.file).toString()
                }
            }
        })
    }
    message = Message().apply { text = messageOrDescription() }
}
