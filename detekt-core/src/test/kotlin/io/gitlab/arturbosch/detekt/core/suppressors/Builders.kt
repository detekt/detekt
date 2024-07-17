package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.createLocation
import org.jetbrains.kotlin.psi.KtElement

internal fun buildFinding(element: KtElement?): Finding = CodeSmell(
    entity = element?.let { Entity.from(element) } ?: buildEmptyEntity(),
    message = "TestMessage",
)

private fun buildEmptyEntity(): Entity = Entity(
    name = "",
    signature = "",
    location = createLocation(""),
    ktElement = compileContentForTest(""),
)

internal fun buildRule(
    vararg pairs: Pair<String, Any>,
): Rule = TestRule(*pairs)

private class TestRule(vararg data: Pair<String, Any>) : Rule(TestConfig(*data), "description")
