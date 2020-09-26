package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.RuleSetId
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations.
 * If this element cannot have annotations, the first annotative parent is searched.
 */
fun KtElement.isSuppressedBy(id: String, aliases: Set<String>, ruleSetId: RuleSetId? = null): Boolean =
    this is KtAnnotated && this.isSuppressedBy(id, aliases, ruleSetId) ||
        findAnnotatedSuppressedParent(id, aliases, ruleSetId)

private fun KtElement.findAnnotatedSuppressedParent(
    id: String,
    aliases: Set<String>,
    ruleSetId: RuleSetId? = null
): Boolean {
    val parent = getStrictParentOfType<KtAnnotated>()

    var suppressed = false
    if (parent != null && parent !is KtFile) {
        suppressed = if (parent.isSuppressedBy(id, aliases, ruleSetId)) {
            true
        } else {
            parent.findAnnotatedSuppressedParent(id, aliases, ruleSetId)
        }
    }

    return suppressed
}

private val detektSuppressionPrefixRegex = Regex("(?i)detekt([.:])")
private const val QUOTES = "\""
private val suppressionAnnotations = setOf("Suppress", "SuppressWarnings")

/**
 * Checks if this kt element is suppressed by @Suppress or @SuppressWarnings annotations.
 */
fun KtAnnotated.isSuppressedBy(id: RuleId, aliases: Set<String>, ruleSetId: RuleSetId? = null): Boolean {
    val acceptedSuppressionIds = mutableSetOf(id, "ALL", "all", "All")
    ruleSetId?.let { acceptedSuppressionIds.addAll(listOf(ruleSetId, "$ruleSetId.$id", "$ruleSetId:$id")) }
    acceptedSuppressionIds.addAll(aliases)
    return annotationEntries
        .find { it.typeReference?.text in suppressionAnnotations }
        ?.run {
            valueArguments
                .map { it.getArgumentExpression()?.text }
                .map { it?.replace(detektSuppressionPrefixRegex, "") }
                .map { it?.replace(QUOTES, "") }
                .find { it in acceptedSuppressionIds }
        } != null
}
