package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.isSuppressedBy
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement

/**
 * Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations.
 * If this element cannot have annotations, the first annotative parent is searched.
 */
@Deprecated(
    "Moved to internal package. Should not be used outside of the rule context.",
    ReplaceWith("isSuppressedBy(id, aliases, null)", "io.gitlab.arturbosch.detekt.api.internal.isSuppressedBy")
)
fun KtElement.isSuppressedBy(id: String, aliases: Set<String>): Boolean = isSuppressedBy(id, aliases, null)

/**
 * Checks if this kt element is suppressed by @Suppress or @SuppressWarnings annotations.
 */
@Deprecated(
    "Moved to internal package. Should not be used outside of the rule context.",
    ReplaceWith("isSuppressedBy(id, aliases)", "io.gitlab.arturbosch.detekt.api.internal.isSuppressedBy")
)
fun KtAnnotated.isSuppressedBy(id: RuleId, aliases: Set<String>): Boolean = isSuppressedBy(id, aliases, null)
