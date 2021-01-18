package io.github.detekt.report.sarif

import io.github.detekt.sarif4j.Result
import io.github.detekt.sarif4j.Run
import io.github.detekt.sarif4j.SarifSchema210
import io.github.detekt.sarif4j.Tool
import io.github.detekt.sarif4j.ToolComponent
import io.github.detekt.tooling.api.VersionProvider
import io.gitlab.arturbosch.detekt.api.Config
import java.net.URI

const val SCHEMA_URL = "https://raw.githubusercontent.com/oasis-tcs/sarif-spec/master/Schemata/sarif-schema-2.1.0.json"

fun sarif(init: SarifSchema210.() -> Unit): SarifSchema210 = SarifSchema210()
    .`with$schema`(URI.create(SCHEMA_URL))
    .withVersion(SarifSchema210.Version._2_1_0)
    .withRuns(ArrayList())
    .apply(init)

fun result(init: Result.() -> Unit): Result = Result().withLocations(ArrayList()).apply(init)

fun tool(init: Tool.() -> Unit): Tool = Tool().apply(init)

fun component(init: ToolComponent.() -> Unit): ToolComponent = ToolComponent().apply(init)

fun SarifSchema210.withDetektRun(config: Config, init: Run.() -> Unit) {
    runs.add(
        Run()
            .withResults(ArrayList())
            .withTool(tool {
                driver = component {
                    guid = "022ca8c2-f6a2-4c95-b107-bb72c43263f3"
                    name = "detekt"
                    fullName = name
                    organization = name
                    language = "en"
                    version = VersionProvider.load().current()
                    semanticVersion = version
                    downloadUri = URI.create("https://github.com/detekt/detekt/releases/download/v$version/detekt")
                    informationUri = URI.create("https://detekt.github.io/detekt")
                    rules = ruleDescriptors(config).values.toSet()
                }
            })
            .apply(init)
    )
}
