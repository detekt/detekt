package io.gitlab.arturbosch.detekt.test

import io.github.detekt.psi.FilePath
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.jetbrains.kotlin.psi.KtElement
import kotlin.io.path.Path

fun createIssue(
    ruleName: String = "TestSmell",
    entity: Entity = createEntity(),
    message: String = "TestMessage",
    severity: Severity = Severity.Error,
    autoCorrectEnabled: Boolean = false,
): Issue = createIssue(
    ruleInfo = createRuleInfo(ruleName),
    entity = entity,
    message = message,
    severity = severity,
    autoCorrectEnabled = autoCorrectEnabled,
)

fun createIssue(
    ruleInfo: Issue.RuleInfo,
    entity: Entity = createEntity(),
    message: String = "TestMessage",
    severity: Severity = Severity.Error,
    autoCorrectEnabled: Boolean = false,
): Issue = IssueImpl(
    ruleInfo = ruleInfo,
    entity = entity,
    message = message,
    severity = severity,
    autoCorrectEnabled = autoCorrectEnabled,
)

fun createRuleInfo(
    id: String = "TestSmell",
    ruleSetId: String = "RuleSet$id",
    description: String = "Description $id",
): Issue.RuleInfo = IssueImpl.RuleInfo(
    id = Rule.Id(id),
    ruleSetId = RuleSet.Id(ruleSetId),
    description = description
)

fun createIssueForRelativePath(
    ruleInfo: Issue.RuleInfo,
    basePath: String = "/Users/tester/detekt/",
    relativePath: String = "TestFile.kt"
): Issue = IssueImpl(
    ruleInfo = ruleInfo,
    entity = Entity(
        name = "TestEntity",
        signature = "TestEntitySignature",
        location = Location(
            source = SourceLocation(1, 1),
            endSource = SourceLocation(1, 1),
            text = TextLocation(0, 0),
            filePath = FilePath.fromRelative(Path(basePath), Path(relativePath))
        ),
        ktElement = null
    ),
    message = "TestMessage"
)

fun createEntity(
    signature: String = "TestEntitySignature",
    location: Location = createLocation(),
    ktElement: KtElement? = null,
) = Entity(
    name = "TestEntity",
    signature = signature,
    location = location,
    ktElement = ktElement
)

fun createLocation(
    path: String = "TestFile.kt",
    basePath: String? = null,
    position: Pair<Int, Int> = 1 to 1,
    endPosition: Pair<Int, Int> = 1 to 1,
    text: IntRange = 0..0,
) = Location(
    source = SourceLocation(position.first, position.second),
    endSource = SourceLocation(endPosition.first, endPosition.second),
    text = TextLocation(text.first, text.last),
    filePath = basePath?.let { FilePath.fromRelative(Path(it), Path(path)) }
        ?: FilePath.fromAbsolute(Path(path)),
)

private data class IssueImpl(
    override val ruleInfo: Issue.RuleInfo,
    override val entity: Entity,
    override val message: String,
    override val severity: Severity = Severity.Error,
    override val autoCorrectEnabled: Boolean = false,
    override val references: List<Entity> = emptyList(),
) : Issue {
    data class RuleInfo(
        override val id: Rule.Id,
        override val ruleSetId: RuleSet.Id,
        override val description: String,
    ) : Issue.RuleInfo
}
