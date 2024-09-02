package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.extractRuleName
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import kotlin.text.RegexOption.IGNORE_CASE

internal fun Rule.isForbiddenSuppress() = this.ruleName == FORBIDDEN_SUPPRESS_NAME
internal fun isForbiddenSuppressById(id: String) = extractRuleName(id) == FORBIDDEN_SUPPRESS_NAME
private val FORBIDDEN_SUPPRESS_NAME = Rule.Name("ForbiddenSuppress")

/**
 * Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations.
 * If this element cannot have annotations, the first annotative parent is searched.
 */
fun KtElement.isSuppressedBy(id: String, aliases: Set<String>, ruleSetId: RuleSet.Id? = null): Boolean {
    val acceptedSuppressionIds = mutableSetOf(id, "ALL", "all", "All")
    if (ruleSetId != null) {
        acceptedSuppressionIds.addAll(listOf(ruleSetId.value, "$ruleSetId.$id", "$ruleSetId:$id"))
    }
    acceptedSuppressionIds.addAll(aliases)

    val r = allAnnotationEntries()
        .filter { it.typeReference?.text in suppressionAnnotations }
        .flatMap { it.valueArguments }
        .mapNotNull { it.getArgumentExpression()?.text }
        .map { it.replace(detektSuppressionPrefixRegex, "") }
        .map { it.replace(QUOTES, "") }
        .any { it in acceptedSuppressionIds }

    if (r && isForbiddenSuppressById(id)) {
        return false
    }

    return r
}

private fun KtElement.allAnnotationEntries(): Sequence<KtAnnotationEntry> {
    val element = this
    return sequence {
        if (element is KtAnnotated) {
            yieldAll(element.annotationEntries)
        }

        element.getStrictParentOfType<KtAnnotated>()?.let { yieldAll(it.allAnnotationEntries()) }
    }
}

private val detektSuppressionPrefixRegex = "detekt[.:]".toRegex(IGNORE_CASE)
private const val QUOTES = "\""
private val suppressionAnnotations = setOf("Suppress", "SuppressWarnings")
