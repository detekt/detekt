package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.jetbrains.kotlin.psi.KtElement
import kotlin.io.path.Path

internal fun buildFinding(element: KtElement?): Finding = CodeSmell(
    entity = element?.let { Entity.from(element) } ?: buildEmptyEntity(),
    message = "TestMessage",
)

private fun buildEmptyEntity(): Entity = Entity(
    name = "",
    signature = "",
    location = Location(
        source = SourceLocation(1, 1),
        endSource = SourceLocation(1, 1),
        text = TextLocation(0, 0),
        path = Path(""),
    ),
    ktElement = compileContentForTest(""),
)

internal fun buildRule(
    vararg pairs: Pair<String, Any>,
): Rule = TestRule(*pairs)

private class TestRule(vararg data: Pair<String, Any>) : Rule(TestConfig(*data), "description")
