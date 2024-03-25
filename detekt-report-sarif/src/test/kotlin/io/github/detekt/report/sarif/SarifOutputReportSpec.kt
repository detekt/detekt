package io.github.detekt.report.sarif

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.whichOS
import io.gitlab.arturbosch.detekt.test.EmptySetupContext
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createIssueForRelativePath
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInfo
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

class SarifOutputReportSpec {

    @Test
    fun `renders multiple issues`() {
        val result = TestDetektion(
            createIssue(
                ruleInfo = createRuleInfo("TestSmellA", "RuleSet1"),
                entity = createEntity(location = createLocation(position = 1 to 1, endPosition = 2 to 3)),
                severity = Severity.Error
            ),
            createIssue(
                ruleInfo = createRuleInfo("TestSmellB", "RuleSet2"),
                entity = createEntity(location = createLocation(position = 3 to 5, endPosition = 3 to 5)),
                severity = Severity.Warning
            ),
            createIssue(
                ruleInfo = createRuleInfo("TestSmellC", "RuleSet2"),
                entity = createEntity(location = createLocation(position = 2 to 1, endPosition = 3 to 1)),
                severity = Severity.Info
            )
        )

        val report = SarifOutputReport()
            .apply { init(EmptySetupContext()) }
            .render(result)

        val expectedReport = readResourceContent("vanilla.sarif.json")
            .replace("<PREFIX>", Path(System.getProperty("user.dir")).toUri().toString())

        assertThat(report).isEqualToIgnoringWhitespace(expectedReport)
    }

    @Test
    fun `renders multiple issues with rule set to warning by default`() {
        val result = TestDetektion(
            createIssue(createRuleInfo("TestSmellA", "RuleSet1"), severity = Severity.Error),
            createIssue(createRuleInfo("TestSmellB", "RuleSet2"), severity = Severity.Warning),
            createIssue(createRuleInfo("TestSmellC", "RuleSet2"), severity = Severity.Info)
        )

        val testConfig = yamlConfig("config_with_rule_set_to_warning.yml")

        val report = SarifOutputReport()
            .apply {
                init(object : SetupContext {
                    override val configUris: Collection<URI> = emptyList()
                    override val config: Config = testConfig
                    override val outputChannel: Appendable = StringBuilder()
                    override val errorChannel: Appendable = StringBuilder()
                    override val properties: MutableMap<String, Any?> = HashMap()
                    override fun register(key: String, value: Any) {
                        properties[key] = value
                    }
                })
            }
            .render(result)

        val expectedReport = readResourceContent("rule_warning.sarif.json")
            .replace("<PREFIX>", Path(System.getProperty("user.dir")).toUri().toString())

        assertThat(report).isEqualToIgnoringWhitespace(expectedReport)
    }

    @Test
    fun `renders multiple issues with relative path`() {
        val basePath = "/Users/tester/detekt/"
        val result = TestDetektion(
            createIssueForRelativePath(createRuleInfo("TestSmellA", "RuleSet1"), basePath = basePath),
            createIssueForRelativePath(createRuleInfo("TestSmellB", "RuleSet2"), basePath = basePath),
            createIssueForRelativePath(createRuleInfo("TestSmellC", "RuleSet2"), basePath = basePath),
        )

        val report = SarifOutputReport()
            .apply {
                init(
                    EmptySetupContext().apply {
                        register(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY, Path(basePath))
                    }
                )
            }
            .render(result)
            .stripWhitespace()

        val expectedReport = readResourceContent("relative_path.sarif.json")

        // Note: GitHub CI uses D: drive, but it could be any drive for local development
        val systemAwareExpectedReport = if (whichOS().startsWith("windows", ignoreCase = true)) {
            val winRoot = Path("/").absolutePathString().replace("\\", "/")
            expectedReport.replace("file:///", "file:///$winRoot")
        } else {
            expectedReport
        }

        assertThat(report).isEqualToIgnoringWhitespace(systemAwareExpectedReport)
    }
}

class TestProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("test")
    override fun instance(): RuleSet = RuleSet(ruleSetId, listOf(::TestRule))
}

class TestRule(config: Config = Config.empty) : Rule(config, "") {
    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        report(CodeSmell(Entity.atName(classOrObject), message = "Error"))
    }
}

private fun String.stripWhitespace() = replace(Regex("\\s"), "")
