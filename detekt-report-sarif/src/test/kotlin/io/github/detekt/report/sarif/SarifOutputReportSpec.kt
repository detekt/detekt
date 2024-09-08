package io.github.detekt.report.sarif

import io.github.detekt.test.utils.readResourceContent
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.TestSetupContext
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createLocation
import io.gitlab.arturbosch.detekt.test.createRuleInstance
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class SarifOutputReportSpec {

    @Test
    fun `renders multiple issues`() {
        val result = TestDetektion(
            createIssue(
                ruleInstance = createRuleInstance("TestSmellA/id", "RuleSet1"),
                entity = createEntity(location = createLocation(position = 1 to 1, endPosition = 2 to 3)),
                severity = Severity.Error
            ),
            createIssue(
                ruleInstance = createRuleInstance("TestSmellD/id", "RuleSet1"),
                entity = createEntity(location = createLocation(position = 1 to 1, endPosition = 2 to 3)),
                severity = Severity.Error,
                suppressReasons = listOf("suppress")
            ),
            createIssue(
                ruleInstance = createRuleInstance("TestSmellB/id", "RuleSet2"),
                entity = createEntity(location = createLocation(position = 3 to 5, endPosition = 3 to 5)),
                severity = Severity.Warning
            ),
            createIssue(
                ruleInstance = createRuleInstance("TestSmellC/id", "RuleSet2"),
                entity = createEntity(location = createLocation(position = 2 to 1, endPosition = 3 to 1)),
                severity = Severity.Info
            )
        )

        val basePath = Path("").absolute()
        val report = SarifOutputReport()
            .apply { init(TestSetupContext(basePath = basePath)) }
            .render(result)

        val expectedReport = readResourceContent("vanilla.sarif.json")
            .replace("<BASE-URL>", basePath.toUri().toString())

        assertThat(report).isEqualToIgnoringWhitespace(expectedReport)
    }

    @Test
    fun `renders multiple issues with rule set to warning by default`() {
        val result = TestDetektion(
            createIssue(createRuleInstance("TestSmellA/id", "RuleSet1"), severity = Severity.Error),
            createIssue(createRuleInstance("TestSmellB/id", "RuleSet2"), severity = Severity.Warning),
            createIssue(createRuleInstance("TestSmellC/id", "RuleSet2"), severity = Severity.Info)
        )

        val testConfig = yamlConfig("config_with_rule_set_to_warning.yml")

        val basePath = Path("").absolute()
        val report = SarifOutputReport()
            .apply { init(TestSetupContext(config = testConfig, basePath = basePath)) }
            .render(result)

        val expectedReport = readResourceContent("rule_warning.sarif.json")
            .replace("<BASE-PATH>", basePath.toUri().toString())

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
