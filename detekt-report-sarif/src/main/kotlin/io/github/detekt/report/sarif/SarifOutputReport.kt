package io.github.detekt.report.sarif

import dev.detekt.api.Config
import dev.detekt.api.Detektion
import dev.detekt.api.OutputReport
import dev.detekt.api.SetupContext
import dev.detekt.api.internal.BuiltInOutputReport
import io.github.detekt.sarif4k.Run
import io.github.detekt.sarif4k.SarifSchema210
import io.github.detekt.sarif4k.SarifSerializer
import io.github.detekt.sarif4k.Tool
import io.github.detekt.sarif4k.ToolComponent
import io.github.detekt.sarif4k.Version

const val SRCROOT = "%SRCROOT%"

class SarifOutputReport : BuiltInOutputReport, OutputReport() {

    override val ending: String = "sarif"
    override val id: String = "sarif"

    private lateinit var config: Config

    override fun init(context: SetupContext) {
        this.config = context.config
    }

    override fun render(detektion: Detektion): String {
        val sarifSchema210 = SarifSchema210(
            schema = "https://docs.oasis-open.org/sarif/sarif/v2.1.0/errata01/os/schemas/sarif-schema-2.1.0.json",
            version = Version.The210,
            runs = listOf(
                Run(
                    tool = Tool(
                        driver = ToolComponent(
                            guid = "022ca8c2-f6a2-4c95-b107-bb72c43263f3",
                            informationURI = "https://detekt.dev",
                            language = "en",
                            name = "detekt",
                            rules = detektion.rules.map { it.toDescriptor() },
                            organization = "detekt",
                        )
                    ),
                    results = toResults(detektion.issues),
                )
            )
        )
        return SarifSerializer.toJson(sarifSchema210)
    }
}
