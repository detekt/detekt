package dev.detekt.core.suppressors

import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Location
import dev.detekt.api.Rule
import dev.detekt.api.SourceLocation
import dev.detekt.api.TextLocation
import dev.detekt.test.TestConfig
import dev.detekt.test.utils.compileContentForTest
import org.jetbrains.kotlin.psi.KtElement
import kotlin.io.path.Path

internal fun buildFinding(element: KtElement?): Finding =
    Finding(
        entity = element?.let { Entity.from(element) } ?: buildEmptyEntity(),
        message = "TestMessage",
    )

private fun buildEmptyEntity(): Entity =
    Entity(
        signature = "",
        location = Location(
            source = SourceLocation(1, 1),
            endSource = SourceLocation(1, 1),
            text = TextLocation(0, 0),
            path = Path(""),
        ),
        ktElement = compileContentForTest(""),
    )

internal fun buildRule(vararg pairs: Pair<String, Any>): Rule = TestRule(*pairs)

private class TestRule(vararg data: Pair<String, Any>) : Rule(TestConfig(*data), "description")
