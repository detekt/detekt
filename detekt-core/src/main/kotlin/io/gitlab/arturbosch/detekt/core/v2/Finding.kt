package io.gitlab.arturbosch.detekt.core.v2

import io.github.detekt.psi.FilePath
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import io.gitlab.arturbosch.detekt.api.v2.Finding
import io.gitlab.arturbosch.detekt.api.v2.Location
import io.gitlab.arturbosch.detekt.api.v2.NewIssue
import io.gitlab.arturbosch.detekt.api.v2.Rule
import io.gitlab.arturbosch.detekt.api.v2.RuleInfo
import io.gitlab.arturbosch.detekt.api.v2.SourceLocation
import io.gitlab.arturbosch.detekt.api.v2.TextLocation
import org.jetbrains.kotlin.psi.KtElement

internal fun NewIssue.toFinding(rule: Rule): Finding {
    return CodeSmell(
        id = entity.signature,
        message = this.message,
        location = entity.location.toV2(),
        severityLevel = this.severity,
        debt = this.debt,
        ktElement = entity.ktElement!!,
        autoCorrectable = this.autoCorrectable,
        rule = RuleInformation(
            id = rule.issue.id,
            ruleSetId = "TODO", // TODO I need to figure out how to use this or if we need it at all
            description = rule.issue.id,
        )
    )
}

private fun io.gitlab.arturbosch.detekt.api.Location.toV2(): Location {
    return LocationImpl(
        source = SourceLocation(source.line, source.column),
        text = TextLocation(text.start, text.end),
        filePath = filePath
    )
}

private data class LocationImpl(
    override val source: SourceLocation,
    override val text: TextLocation,
    override val filePath: FilePath
) : Location

private data class CodeSmell(
    override val id: String,
    override val message: String,
    override val location: Location,
    override val severityLevel: SeverityLevel,
    override val debt: Debt,
    override val ktElement: KtElement,
    override val autoCorrectable: Boolean,
    override val rule: RuleInfo
) : Finding

private data class RuleInformation(
    override val id: String,
    override val ruleSetId: String,
    override val description: String
) : RuleInfo
