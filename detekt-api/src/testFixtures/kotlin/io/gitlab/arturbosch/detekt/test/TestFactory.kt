package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.internal.FakeKtElement
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.jetbrains.kotlin.psi.KtElement
import java.nio.file.Path
import kotlin.io.path.Path

fun createIssue(
    ruleId: String = "TestSmell/id",
    entity: Issue.Entity = createEntity(),
    message: String = "TestMessage",
    severity: Severity = Severity.Error,
    suppressReasons: List<String> = emptyList(),
): Issue = createIssue(
    ruleInstance = createRuleInstance(ruleId),
    entity = entity,
    message = message,
    severity = severity,
    suppressReasons = suppressReasons,
)

fun createIssue(
    ruleInstance: RuleInstance,
    entity: Issue.Entity = createEntity(),
    message: String = "TestMessage",
    severity: Severity = Severity.Error,
    suppressReasons: List<String> = emptyList(),
): Issue = IssueImpl(
    ruleInstance = ruleInstance,
    entity = entity,
    message = message,
    severity = severity,
    suppressReasons = suppressReasons,
)

fun createIssue(
    ruleInstance: RuleInstance,
    location: Issue.Location,
    message: String = "TestMessage",
    severity: Severity = Severity.Error,
    suppressReasons: List<String> = emptyList(),
): Issue = IssueImpl(
    ruleInstance = ruleInstance,
    entity = createEntity(location = location),
    message = message,
    severity = severity,
    suppressReasons = suppressReasons,
)

fun createRuleInstance(
    id: String = "TestSmell/id",
    ruleSetId: String = "RuleSet${id.split("/", limit = 2).first()}",
    description: String = "Description ${id.split("/", limit = 2).first()}",
): RuleInstance {
    val split = id.split("/", limit = 2)
    return RuleInstanceImpl(
        id = id,
        name = Rule.Name(split.first()),
        ruleSetId = RuleSet.Id(ruleSetId),
        description = description
    )
}

fun createEntity(
    signature: String = "TestEntitySignature",
    location: Issue.Location = createLocation(),
    ktElement: KtElement = FakeKtElement(),
): Issue.Entity = IssueImpl.Entity(
    name = "TestEntity",
    signature = signature,
    location = location,
    ktElement = ktElement
)

fun createLocation(
    path: String = "TestFile.kt",
    position: Pair<Int, Int> = 1 to 1,
    endPosition: Pair<Int, Int> = 1 to 1,
    text: IntRange = 0..0,
): Issue.Location {
    require(!path.startsWith("/")) { "The path shouldn't start with '/'" }
    return IssueImpl.Location(
        source = SourceLocation(position.first, position.second),
        endSource = SourceLocation(endPosition.first, endPosition.second),
        text = TextLocation(text.first, text.last),
        path = Path(path),
    )
}

private data class IssueImpl(
    override val ruleInstance: RuleInstance,
    override val entity: Issue.Entity,
    override val message: String,
    override val severity: Severity = Severity.Error,
    override val references: List<Issue.Entity> = emptyList(),
    override val suppressReasons: List<String>
) : Issue {
    data class Entity(
        override val name: String,
        override val signature: String,
        override val location: Issue.Location,
        override val ktElement: KtElement
    ) : Issue.Entity

    data class Location(
        override val source: SourceLocation,
        override val endSource: SourceLocation,
        override val text: TextLocation,
        override val path: Path
    ) : Issue.Location {
        init {
            require(!path.isAbsolute) { "Path should be always relative" }
        }
    }
}

private data class RuleInstanceImpl(
    override val id: String,
    override val name: Rule.Name,
    override val ruleSetId: RuleSet.Id,
    override val description: String,
) : RuleInstance
