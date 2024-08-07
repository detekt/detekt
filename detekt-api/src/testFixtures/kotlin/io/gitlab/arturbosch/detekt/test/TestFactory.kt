package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.internal.FakeKtElement
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleInstance
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.jetbrains.kotlin.psi.KtElement
import kotlin.io.path.Path
import kotlin.io.path.absolute

fun createIssue(
    ruleId: String = "TestSmell/id",
    entity: Entity = createEntity(),
    message: String = "TestMessage",
    severity: Severity = Severity.Error,
    autoCorrectEnabled: Boolean = false,
): Issue = createIssue(
    ruleInstance = createRuleInstance(ruleId),
    entity = entity,
    message = message,
    severity = severity,
    autoCorrectEnabled = autoCorrectEnabled,
)

fun createIssue(
    ruleInstance: RuleInstance,
    entity: Entity = createEntity(),
    message: String = "TestMessage",
    severity: Severity = Severity.Error,
    autoCorrectEnabled: Boolean = false,
): Issue = IssueImpl(
    ruleInstance = ruleInstance,
    entity = entity,
    message = message,
    severity = severity,
    autoCorrectEnabled = autoCorrectEnabled,
)

fun createIssue(
    ruleInstance: RuleInstance,
    location: Location,
    message: String = "TestMessage",
    severity: Severity = Severity.Error,
    autoCorrectEnabled: Boolean = false,
): Issue = IssueImpl(
    ruleInstance = ruleInstance,
    entity = createEntity(location = location),
    message = message,
    severity = severity,
    autoCorrectEnabled = autoCorrectEnabled,
)

fun createRuleInstance(
    id: String = "TestSmell/id",
    ruleSetId: String = "RuleSet${id.split("/", limit = 2).first()}",
    description: String = "Description ${id.split("/", limit = 2).first()}",
): RuleInstance = RuleInstanceImpl(
    id = Rule.Id(id),
    ruleSetId = RuleSet.Id(ruleSetId),
    description = description
)

fun createIssueForRelativePath(
    ruleInstance: RuleInstance,
    basePath: String = "Users/tester/detekt/",
    relativePath: String = "TestFile.kt"
): Issue =
    IssueImpl(
        ruleInstance = ruleInstance,
        entity = createEntity(
            location = Location(
                source = SourceLocation(1, 1),
                endSource = SourceLocation(1, 1),
                text = TextLocation(0, 0),
                path = Path("/").absolute().resolve(basePath).resolve(relativePath)
            ),
        ),
        message = "TestMessage"
    )

fun createEntity(
    signature: String = "TestEntitySignature",
    location: Location = createLocation(),
    ktElement: KtElement = FakeKtElement(),
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
): Location {
    require(!path.startsWith("/")) { "The path shouldn't start with '/'" }
    return Location(
        source = SourceLocation(position.first, position.second),
        endSource = SourceLocation(endPosition.first, endPosition.second),
        text = TextLocation(text.first, text.last),
        path = basePath?.let { Path(it).absolute().resolve(path) } ?: Path(path).absolute(),
    )
}

private data class IssueImpl(
    override val ruleInstance: RuleInstance,
    override val entity: Entity,
    override val message: String,
    override val severity: Severity = Severity.Error,
    override val autoCorrectEnabled: Boolean = false,
    override val references: List<Entity> = emptyList(),
) : Issue

private data class RuleInstanceImpl(
    override val id: Rule.Id,
    override val ruleSetId: RuleSet.Id,
    override val description: String,
) : RuleInstance
