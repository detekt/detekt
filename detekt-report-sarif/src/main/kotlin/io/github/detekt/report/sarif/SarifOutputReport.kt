package io.github.detekt.report.sarif

import io.github.detekt.psi.toUnifiedString
import io.github.detekt.sarif4j.ArtifactLocation
import io.github.detekt.sarif4j.JacksonSarifWriter
import io.github.detekt.sarif4j.Location
import io.github.detekt.sarif4j.Message
import io.github.detekt.sarif4j.OriginalUriBaseIds
import io.github.detekt.sarif4j.PhysicalLocation
import io.github.detekt.sarif4j.Region
import io.github.detekt.sarif4j.Result
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.getOrNull
import java.nio.file.Path

const val DETEKT_OUTPUT_REPORT_BASE_PATH_KEY = "detekt.output.report.base.path"
const val SARIF_SRCROOT_PROPERTY = "%SRCROOT%"

class SarifOutputReport : OutputReport() {

    override val ending: String = "sarif"
    override val id: String = "sarif"
    override val name = "SARIF: a standard format for the output of static analysis tools"

    private var config: Config by SingleAssign()
    private var basePath: String? = null

    @OptIn(UnstableApi::class)
    override fun init(context: SetupContext) {
        this.config = context.config
        this.basePath = context.getOrNull<Path>(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY)?.toAbsolutePath()?.toUnifiedString()
    }

    override fun render(detektion: Detektion): String {
        val report = sarif {
            withDetektRun(config) {
                basePath?.let {
                    originalUriBaseIds = OriginalUriBaseIds()
                        .withAdditionalProperty(
                            SARIF_SRCROOT_PROPERTY,
                            ArtifactLocation().withUri("file://$basePath")
                        )
                }
                for ((ruleSetId, findings) in detektion.findings) {
                    for (finding in findings) {
                        results.add(finding.toResult(ruleSetId))
                    }
                }
            }
        }
        return JacksonSarifWriter().toJson(report)
    }
}

private fun SeverityLevel.toResultLevel() = when (this) {
    SeverityLevel.ERROR -> Result.Level.ERROR
    SeverityLevel.WARNING -> Result.Level.WARNING
    SeverityLevel.INFO -> Result.Level.NOTE
}

private fun Finding.toResult(ruleSetId: RuleSetId): Result = result {
    ruleId = "detekt.$ruleSetId.$id"
    level = severity.toResultLevel()
    for (location in listOf(location) + references.map { it.location }) {
        locations.add(Location().apply {
            physicalLocation = PhysicalLocation().apply {
                region = Region().apply {
                    startLine = location.source.line
                    startColumn = location.source.column
                }
                artifactLocation = ArtifactLocation().apply {
                    if (location.filePath.relativePath != null) {
                        uri = location.filePath.relativePath?.toUnifiedString()
                        uriBaseId = SARIF_SRCROOT_PROPERTY
                    } else {
                        uri = location.filePath.absolutePath.toUnifiedString()
                    }
                }
            }
        })
    }
    message = Message().apply { text = messageOrDescription() }
}
