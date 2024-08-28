package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import kotlin.text.RegexOption.IGNORE_CASE

internal fun Rule.isForbiddenSuppress() = this::class.qualifiedName == FORBIDDEN_SUPPRESS_QNAME
private const val FORBIDDEN_SUPPRESS_QNAME = "io.gitlab.arturbosch.detekt.rules.style.ForbiddenSuppress"
internal fun warnForbiddenSuppressCannotBeSuppressed() {
    println("Warning: ForbiddenSuppress cannot be suppressed")
}

/**
 * Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations.
 * If this element cannot have annotations, the first annotative parent is searched.
 */
fun KtElement.isSuppressedBy(rule: Rule, id: String, aliases: Set<String>, ruleSetId: RuleSet.Id? = null): Boolean {
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

    if (r && rule.isForbiddenSuppress()) {
        warnForbiddenSuppressCannotBeSuppressed()
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
