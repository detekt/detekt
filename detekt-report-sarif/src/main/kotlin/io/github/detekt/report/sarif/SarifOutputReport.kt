package io.github.detekt.report.sarif

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
import io.gitlab.arturbosch.detekt.api.internal.BuiltInOutputReport
import io.gitlab.arturbosch.detekt.api.internal.whichDetekt
import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString

const val SRCROOT = "%SRCROOT%"

class SarifOutputReport : BuiltInOutputReport, OutputReport() {

    override val ending: String = "sarif"
    override val id: String = "sarif"

    private lateinit var basePath: String
    private lateinit var config: Config

    override fun init(context: SetupContext) {
        this.basePath = context.basePath
            .invariantSeparatorsPathString
            .let { if (!it.endsWith("/")) "$it/" else it }

        this.config = context.config
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
                            informationURI = "https://detekt.dev",
                            language = "en",
                            name = "detekt",
                            rules = toReportingDescriptors(config),
                            organization = "detekt",
                            semanticVersion = version,
                            version = version
                        )
                    ),
                    originalURIBaseIDS = mapOf(SRCROOT to ArtifactLocation(uri = Path(basePath).toUri().toString())),
                    results = toResults(detektion)
                )
            )
        )
        return SarifSerializer.toJson(sarifSchema210)
    }
}
