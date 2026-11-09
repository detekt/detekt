package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaLocalVariableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.idea.references.KtReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Detects subjectless `when` expressions with type checks against one stable value that can be
 * rewritten as a subject `when` and compiled as a JVM invokedynamic type switch.
 *
 * A subject `when` evaluates its subject once and supports type and `null` labels. This rule only
 * reports checks against stable references, avoiding suggestions that could change behavior by
 * evaluating a getter or function call fewer times.
 *
 * <noncompliant>
 * fun classify(value: Any?) = when {
 *     value is String -> 1
 *     value is Number -> 2
 *     value == null -> 3
 *     else -> 4
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun classify(value: Any?) = when (value) {
 *     is String -> 1
 *     is Number -> 2
 *     null -> 3
 *     else -> 4
 * }
 * </compliant>
 */
class WhenIndyDirectCandidate(config: Config) :
    Rule(
        config,
        "A subjectless `when` expression with type checks against one stable value can use a subject to enable " +
            "JVM invokedynamic type-switch compilation."
    ),
    RequiresAnalysisApi {

    override fun visitWhenExpression(expression: KtWhenExpression) {
        super.visitWhenExpression(expression)
        val candidate = expression.directCandidate() ?: return

        report(
            Finding(
                Entity.from(expression),
                "Rewrite this as `when (${candidate.subject})` to enable JVM invokedynamic type-switch " +
                    "compilation for its ${candidate.labels.joinToString()} checks."
            )
        )
    }

    private fun KtWhenExpression.directCandidate(): SubjectTypeTests? {
        if (subjectExpression != null) return null
        val tests = directTests() ?: return null
        return tests.combinedForOneSubject()
            ?.takeIf { it.labels.distinct().size >= MINIMUM_TYPE_LABELS }
    }

    private fun KtWhenExpression.directTests(): List<SubjectTypeTests>? =
        entries
            .filterNot { it.isElse }
            .takeIf { nonElseEntries ->
                nonElseEntries.isNotEmpty() && nonElseEntries.none { it.guard != null }
            }
            ?.flatMap { entry ->
                entry.conditions.map { condition ->
                    (condition as? KtWhenConditionWithExpression)?.expression?.directTypeTests()
                }
            }
            ?.takeIf { tests -> tests.all { it != null } }
            ?.filterNotNull()

    private fun KtExpression.directTypeTests(): SubjectTypeTests? =
        when (val expression = withoutParentheses()) {
            is KtIsExpression -> expression.toSubjectTypeTests()

            is KtBinaryExpression -> when (expression.operationToken) {
                KtTokens.OROR ->
                    expression.combineBinarySides()

                KtTokens.EQEQ, KtTokens.EQEQEQ ->
                    expression.nullCheck()

                else ->
                    null
            }

            else ->
                null
        }

    private fun KtIsExpression.toSubjectTypeTests(): SubjectTypeTests? {
        val type = typeReference ?: return null
        if (isNegated || type.isUnsupportedTypeCheck()) return null
        return SubjectTypeTests(leftHandSide.stableSubjectText() ?: return null, listOf(type.text))
    }

    private fun KtBinaryExpression.combineBinarySides(): SubjectTypeTests? {
        val leftTests = left?.directTypeTests() ?: return null
        val rightTests = right?.directTypeTests() ?: return null
        if (leftTests.subject != rightTests.subject) return null
        return SubjectTypeTests(leftTests.subject, leftTests.labels + rightTests.labels)
    }

    private fun KtBinaryExpression.nullCheck(): SubjectTypeTests? =
        when {
            left?.isNullLiteral() == true ->
                right?.stableSubjectText()?.let { SubjectTypeTests(it, listOf(NULL_LABEL)) }

            right?.isNullLiteral() == true ->
                left?.stableSubjectText()?.let { SubjectTypeTests(it, listOf(NULL_LABEL)) }

            else ->
                null
        }

    private fun List<SubjectTypeTests>.combinedForOneSubject(): SubjectTypeTests? {
        val first = firstOrNull() ?: return null
        if (any { it.subject != first.subject }) return null
        return SubjectTypeTests(first.subject, flatMap(SubjectTypeTests::labels))
    }

    private fun KtExpression.stableSubjectText(): String? {
        val expression = withoutParentheses()
        return when (expression) {
            is KtNameReferenceExpression ->
                expression.text.takeIf { expression.resolvesToStableValue() }

            is KtThisExpression ->
                expression.text

            else ->
                null
        }
    }

    @OptIn(KaExperimentalApi::class)
    private fun KtNameReferenceExpression.resolvesToStableValue(): Boolean =
        analyze(this) {
            val symbol = references
                .asSequence()
                .filterIsInstance<KtReference>()
                .flatMap { it.resolveToSymbols().asSequence() }
                .singleOrNull()
                ?: return@analyze false

            when (symbol) {
                is KaValueParameterSymbol ->
                    symbol.generatedPrimaryConstructorProperty?.isVal != false

                is KaLocalVariableSymbol ->
                    symbol.isVal && (symbol.psi as? KtProperty)?.delegateExpression == null

                else ->
                    false
            }
        }

    private companion object {
        const val MINIMUM_TYPE_LABELS = 2
        const val NULL_LABEL = "null"
    }
}

private val MUTABLE_COLLECTION_TYPES = setOf(
    "kotlin.collections.MutableCollection",
    "kotlin.collections.MutableIterable",
    "kotlin.collections.MutableIterator",
    "kotlin.collections.MutableList",
    "kotlin.collections.MutableListIterator",
    "kotlin.collections.MutableMap",
    "kotlin.collections.MutableMap.MutableEntry",
    "kotlin.collections.MutableSet",
)

private fun KtExpression.withoutParentheses(): KtExpression =
    if (this is KtParenthesizedExpression) expression?.withoutParentheses() ?: this else this

private fun KtExpression.isNullLiteral(): Boolean {
    val expression = withoutParentheses()
    return expression is KtConstantExpression && expression.node.elementType == KtNodeTypes.NULL
}

private fun KtTypeReference.isUnsupportedTypeCheck(): Boolean =
    analyze(this) {
        val resolvedType = this@isUnsupportedTypeCheck.type.fullyExpandedType
        if (resolvedType.isFunctionType) return@analyze true

        val className = (resolvedType as? KaClassType)
            ?.classId
            ?.asSingleFqName()
            ?.asString()
            ?: return@analyze true
        className in MUTABLE_COLLECTION_TYPES
    }

private data class SubjectTypeTests(val subject: String, val labels: List<String>)
