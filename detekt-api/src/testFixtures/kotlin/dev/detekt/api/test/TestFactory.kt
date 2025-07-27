package dev.detekt.api.test

import dev.detekt.api.Issue
import dev.detekt.api.RuleInstance
import dev.detekt.api.RuleSet
import dev.detekt.api.Severity
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import java.net.URI
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
): Issue = Issue(
    ruleInstance = ruleInstance,
    entity = entity,
    references = emptyList(),
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
): Issue = Issue(
    ruleInstance = ruleInstance,
    entity = createEntity(location = location),
    references = emptyList(),
    message = message,
    severity = severity,
    suppressReasons = suppressReasons,
)

@Suppress("LongParameterList")
fun createRuleInstance(
    id: String = "TestSmell/id",
    ruleSetId: String = "RuleSet${id.substringBefore("/")}",
    url: String? = null,
    description: String = "Description ${id.substringBefore("/")}",
    severity: Severity = Severity.Error,
    active: Boolean = true,
): RuleInstance = RuleInstance(
    id = id,
    ruleSetId = RuleSet.Id(ruleSetId),
    url = url?.let(::URI),
    description = description,
    severity = severity,
    active = active,
)

fun createEntity(
    signature: String = "TestEntitySignature",
    location: Issue.Location = createLocation(),
): Issue.Entity = Issue.Entity(
    signature = signature,
    location = location,
)

fun createLocation(
    path: String = "TestFile.kt",
    position: Pair<Int, Int> = 1 to 1,
    endPosition: Pair<Int, Int> = 1 to 1,
    text: IntRange = 0..0,
): Issue.Location {
    require(!path.startsWith("/")) { "The path shouldn't start with '/'" }
    return Issue.Location(
        source = SourceLocation(position.first, position.second),
        endSource = SourceLocation(endPosition.first, endPosition.second),
        text = TextLocation(text.first, text.last),
        path = Path(path),
    )
}
