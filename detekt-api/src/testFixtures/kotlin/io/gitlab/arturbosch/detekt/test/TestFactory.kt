package io.gitlab.arturbosch.detekt.test

import io.github.detekt.psi.FilePath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.jetbrains.kotlin.psi.KtElement
import kotlin.io.path.Path

fun createFinding(
    ruleName: String = "TestSmell",
    fileName: String = "TestFile.kt",
    entity: Entity = createEntity(location = createLocation(fileName)),
    severity: Severity = Severity.ERROR
) = createFinding(createIssue(ruleName), entity, "TestMessage", severity)

fun createCorrectableFinding(
    ruleName: String = "TestSmell",
    fileName: String = "TestFile.kt",
    severity: Severity = Severity.ERROR
) = object : CorrectableCodeSmell(
    issue = createIssue(ruleName),
    entity = createEntity(location = createLocation(fileName)),
    message = "TestMessage",
    autoCorrectEnabled = true
) {
    override val severity: Severity
        get() = severity
}

fun createFinding(
    issue: Issue,
    entity: Entity,
    message: String = "TestMessage",
    severity: Severity = Severity.ERROR
) = object : CodeSmell(
    issue = issue,
    entity = entity,
    message = message
) {
    override val severity: Severity
        get() = severity
}

fun createFindingForRelativePath(
    ruleName: String = "TestSmell",
    basePath: String = "/Users/tester/detekt/",
    relativePath: String = "TestFile.kt"
) = CodeSmell(
    issue = createIssue(ruleName),
    entity = Entity(
        name = "TestEntity",
        signature = "TestEntitySignature",
        location = Location(
            source = SourceLocation(1, 1),
            text = TextLocation(0, 0),
            filePath = FilePath.fromRelative(Path(basePath), Path(relativePath))
        ),
        ktElement = null
    ),
    message = "TestMessage"
)

fun createIssue(id: String) = Issue(
    id = id,
    description = "Description $id",
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
    path: String = "File.kt",
    basePath: String? = null,
    position: Pair<Int, Int> = 1 to 1,
    text: IntRange = 0..0,
) = Location(
    source = SourceLocation(position.first, position.second),
    text = TextLocation(text.first, text.last),
    filePath = basePath?.let { FilePath.fromRelative(Path(it), Path(path)) }
        ?: FilePath.fromAbsolute(Path(path)),
)
