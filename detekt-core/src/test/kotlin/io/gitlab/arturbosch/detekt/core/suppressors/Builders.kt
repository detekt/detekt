package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.createLocation
import org.jetbrains.kotlin.psi.KtElement

internal fun buildFinding(element: KtElement?): Finding = CodeSmell(
    issue = Issue("RuleName", Severity.CodeSmell, "", Debt.FIVE_MINS),
    entity = element?.let { Entity.from(element) } ?: buildEmptyEntity(),
    message = "",
)

private fun buildEmptyEntity(): Entity = Entity(
    name = "",
    signature = "",
    location = createLocation(path = "/"),
    ktElement = null,
)

internal fun buildConfigAware(
    vararg pairs: Pair<String, Any>
) = object : ConfigAware {
    override val ruleId: RuleId = "ruleId"
    override val ruleSetConfig: Config = TestConfig(*pairs)
}
