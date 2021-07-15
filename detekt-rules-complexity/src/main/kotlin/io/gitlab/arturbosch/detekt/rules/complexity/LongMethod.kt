package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.metrics.linesOfCode
import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.utils.addToStdlib.flattenTo
import java.util.IdentityHashMap

/**
 * Methods should have one responsibility. Long methods can indicate that a method handles too many cases at once.
 * Prefer smaller methods with clear names that describe their functionality clearly.
 *
 * Extract parts of the functionality of long methods into separate, smaller methods.
 */
@ActiveByDefault(since = "1.0.0")
class LongMethod(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "LongMethod",
        Severity.Maintainability,
        "One method should have one responsibility. Long methods tend to handle many things at once. " +
            "Prefer smaller methods to make them easier to understand.",
        Debt.TWENTY_MINS
    )

    @Configuration("number of lines in a method to trigger the rule")
    private val threshold: Int by config(defaultValue = 60)

    @Configuration("ignore long methods in the context of these annotation class names")
    private val ignoreAnnotated: List<String> by config(emptyList())

    private val functionToLinesCache = HashMap<KtNamedFunction, Int>()
    private val functionToBodyLinesCache = HashMap<KtNamedFunction, Int>()
    private val nestedFunctionTracking = IdentityHashMap<KtNamedFunction, HashSet<KtNamedFunction>>()

    private lateinit var annotationExcluder: AnnotationExcluder

    override fun preVisit(root: KtFile) {
        functionToLinesCache.clear()
        functionToBodyLinesCache.clear()
        nestedFunctionTracking.clear()
    }

    override fun postVisit(root: KtFile) {
        val functionToLines = HashMap<KtNamedFunction, Int>()
        functionToLinesCache.map { (function, lines) ->
            val isNested = function.getStrictParentOfType<KtNamedFunction>() != null
            if (isNested) functionToLines[function] = functionToBodyLinesCache[function] ?: 0
            else functionToLines[function] = lines
        }
        for ((function, lines) in functionToLines) {
            if (lines >= threshold) {
                report(
                    ThresholdedCodeSmell(
                        issue,
                        Entity.atName(function),
                        Metric("SIZE", lines, threshold),
                        "The function ${function.nameAsSafeName} is too long ($lines). " +
                            "The maximum length is $threshold."
                    )
                )
            }
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (annotationExcluder.shouldExclude(function.annotationEntries)) return

        val parentMethods = function.getStrictParentOfType<KtNamedFunction>()
        val bodyEntity = function.bodyBlockExpression ?: function.bodyExpression
        val lines = (if (parentMethods != null) function else bodyEntity)?.linesOfCode() ?: 0
        functionToLinesCache[function] = lines
        functionToBodyLinesCache[function] = bodyEntity?.linesOfCode() ?: 0
        parentMethods?.let { nestedFunctionTracking.getOrPut(it) { HashSet() }.add(function) }
        super.visitNamedFunction(function)
        findAllNestedFunctions(function)
            .fold(0) { acc, next -> acc + (functionToLinesCache[next] ?: 0) }
            .takeIf { it > 0 }
            ?.let { functionToLinesCache[function] = lines - it }
    }

    override fun visitKtFile(file: KtFile) {
        annotationExcluder = AnnotationExcluder(file, ignoreAnnotated)
        super.visitKtFile(file)
    }

    private fun findAllNestedFunctions(startFunction: KtNamedFunction): Sequence<KtNamedFunction> = sequence {
        var nestedFunctions = nestedFunctionTracking[startFunction]
        while (!nestedFunctions.isNullOrEmpty()) {
            yieldAll(nestedFunctions)
            nestedFunctions = nestedFunctions.mapNotNull { nestedFunctionTracking[it] }.flattenTo(HashSet())
        }
    }
}
