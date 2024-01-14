package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.psi.FilePath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.createIssue
import org.jetbrains.kotlin.psi.KtElement
import kotlin.io.path.Path

internal fun buildFinding(element: KtElement?): Finding = CodeSmell(
    issue = createIssue("RuleName"),
    entity = element?.let { Entity.from(element) } ?: buildEmptyEntity(),
    message = "TestMessage",
)

private fun buildEmptyEntity(): Entity = Entity(
    name = "",
    signature = "",
    location = Location(
        source = SourceLocation(0, 0),
        text = TextLocation(0, 0),
        filePath = FilePath.fromAbsolute(Path("/"))
    ),
    ktElement = null,
)

internal fun buildRule(
    vararg pairs: Pair<String, Any>,
): Rule = TestRule(*pairs)

private class TestRule(vararg data: Pair<String, Any>) : Rule(TestConfig(*data), "description")
