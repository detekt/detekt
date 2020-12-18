package io.gitlab.arturbosch.detekt.test

import io.github.detekt.psi.FilePath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import org.jetbrains.kotlin.psi.KtElement

fun createFinding(ruleName: String = "TestSmell", fileName: String = "TestFile.kt") =
    CodeSmell(createIssue(ruleName), createEntity(fileName), "TestMessage")

fun createCorrectableFinding(ruleName: String = "TestSmell", fileName: String = "TestFile.kt") =
    CorrectableCodeSmell(createIssue(ruleName), createEntity(fileName), "TestMessage", autoCorrectEnabled = true)

fun createFinding(
    issue: Issue,
    entity: Entity,
    message: String = entity.signature,
) = CodeSmell(
    issue = issue,
    entity = entity,
    message = message
)

fun createIssue(id: String) = Issue(
    id = id,
    severity = Severity.CodeSmell,
    description = "Description $id",
    debt = Debt.FIVE_MINS
)

fun createEntity(
    path: String,
    position: Pair<Int, Int> = 1 to 1,
    text: IntRange = 0..0,
    ktElement: KtElement? = null,
    baseDir: String? = null
) = Entity(
    name = "TestEntity",
    signature = "TestEntitySignature",
    location = Location(
        source = SourceLocation(position.first, position.second),
        text = TextLocation(text.first, text.last),
        filePath = baseDir?.let { FilePath.fromRelative(it, path) } ?: FilePath.fromAbsolute(path)
    ),
    ktElement = ktElement
)

fun createFindingFromRelativePath(
    ruleName: String = "TestSmell",
    baseDir: String = "/Users/tester/detekt/",
    fileName: String = "TestFile.kt"
) = CodeSmell(
    issue = createIssue(ruleName),
    entity = Entity(
        name = "TestEntity",
        signature = "TestEntitySignature",
        location = Location(
            source = SourceLocation(1, 1),
            text = TextLocation(0, 0),
            filePath = FilePath.fromRelative(baseDir, fileName)
        ),
        ktElement = null
    ),
    message = "TestMessage"
)
