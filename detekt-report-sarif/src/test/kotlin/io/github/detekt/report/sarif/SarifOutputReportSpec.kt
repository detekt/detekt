package io.github.detekt.report.sarif

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.OutputReport.Companion.DETEKT_OUTPUT_REPORT_BASE_PATH_KEY
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.EmptySetupContext
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createIssueForRelativePath
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.junit.jupiter.api.Test
import java.net.URI
import kotlin.io.path.Path
import kotlin.io.path.absolute

class SarifOutputReportSpec {

    @Test
    fun `renders multiple issues`() {
        val result = TestDetektion(
            createIssue(
                ruleInstance = createRuleInstance("TestSmellA", "RuleSet1"),
                entity = createEntity(location = createLocation(position = 1 to 1, endPosition = 2 to 3)),
                severity = Severity.Error
            ),
            createIssue(
                ruleInstance = createRuleInstance("TestSmellB", "RuleSet2"),
                entity = createEntity(location = createLocation(position = 3 to 5, endPosition = 3 to 5)),
                severity = Severity.Warning
            ),
            createIssue(
                ruleInstance = createRuleInstance("TestSmellC", "RuleSet2"),
                entity = createEntity(location = createLocation(position = 2 to 1, endPosition = 3 to 1)),
                severity = Severity.Info
            )
        )

        val report = SarifOutputReport()
            .apply { init(EmptySetupContext()) }
            .render(result)

        val expectedReport = readResourceContent("vanilla.sarif.json")
            .replace("<PREFIX>", Path("").toUri().toString())

        assertThat(report).isEqualToIgnoringWhitespace(expectedReport)
    }

    @Test
    fun `renders multiple issues with rule set to warning by default`() {
        val result = TestDetektion(
            createIssue(createRuleInstance("TestSmellA", "RuleSet1"), severity = Severity.Error),
            createIssue(createRuleInstance("TestSmellB", "RuleSet2"), severity = Severity.Warning),
            createIssue(createRuleInstance("TestSmellC", "RuleSet2"), severity = Severity.Info)
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
            .replace("<PREFIX>", Path("").toUri().toString())

        assertThat(report).isEqualToIgnoringWhitespace(expectedReport)
    }

    @Test
    fun `renders multiple issues with relative path`() {
        val result = TestDetektion(
            createIssueForRelativePath(createRuleInstance("TestSmellA", "RuleSet1")),
            createIssueForRelativePath(createRuleInstance("TestSmellB", "RuleSet2")),
            createIssueForRelativePath(createRuleInstance("TestSmellC", "RuleSet2")),
        )

        val basePath = Path("/").absolute().resolve("Users/tester/detekt/")
        val report = SarifOutputReport()
            .apply {
                init(
                    EmptySetupContext().apply {
                        register(DETEKT_OUTPUT_REPORT_BASE_PATH_KEY, basePath)
                    }
                )
            }
            .render(result)
            .stripWhitespace()

        val expectedReport = readResourceContent("relative_path.sarif.json")
            .replace("<BASE_URI>", basePath.toUri().toString())

        assertThat(report).isEqualToIgnoringWhitespace(expectedReport)
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
