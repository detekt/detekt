package io.github.detekt.report.sarif

import io.github.detekt.psi.toUnifiedString
import io.github.detekt.sarif4k.ArtifactLocation
import io.github.detekt.sarif4k.Run
import io.github.detekt.sarif4k.SarifSchema210
import io.github.detekt.sarif4k.SarifSerializer
import io.github.detekt.sarif4k.Tool
import io.github.detekt.sarif4k.ToolComponent
import io.github.detekt.sarif4k.Version
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.getOrNull
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import java.nio.file.Path

const val DETEKT_OUTPUT_REPORT_BASE_PATH_KEY = "detekt.output.report.base.path"
const val SRCROOT = "%SRCROOT%"

class SarifOutputReport : OutputReport() {

    override val ending: String = "sarif"
    override val id: String = "sarif"
    override val name = "SARIF: a standard format for the output of static analysis tools"

    private var config: Config by SingleAssign()
    private var basePath: String? = null

    @OptIn(UnstableApi::class)
    override fun init(context: SetupContext) {
        this.config = context.config
        this.basePath = context.getOrNull<Path>(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY)
            ?.toAbsolutePath()
            ?.toUnifiedString()
            ?.let {
                if (!it.endsWith("/")) "$it/" else it
            }
    }

    override fun render(detektion: Detektion): String {
        val version = whichDetekt()
        val sarifSchema210 = SarifSchema210(
            schema = "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json",
            version = Version.The210,
            runs = listOf(
                Run(
                    tool = Tool(
                        driver = ToolComponent(
                            downloadURI = "https://github.com/detekt/detekt/releases/download/v$version/detekt",
                            fullName = "detekt",
                            guid = "022ca8c2-f6a2-4c95-b107-bb72c43263f3",
                            informationURI = "https://detekt.github.io/detekt",
                            language = "en",
                            name = "detekt",
                            rules = toReportingDescriptors(config),
                            organization = "detekt",
                            semanticVersion = version,
                            version = version
                        )
                    ),
                    originalURIBaseIDS = basePath?.let { mapOf(SRCROOT to ArtifactLocation(uri = "file://$basePath")) },
                    results = toResults(detektion)
                )
            )
        )
        return SarifSerializer.toJson(sarifSchema210)
    }
}
