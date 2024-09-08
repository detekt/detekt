package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.metrics.linesOfCode
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import java.util.IdentityHashMap

/**
 * Methods should have one responsibility. Long methods can indicate that a method handles too many cases at once.
 * Prefer smaller methods with clear names that describe their functionality clearly.
 *
 * Extract parts of the functionality of long methods into separate, smaller methods.
 */
@ActiveByDefault(since = "1.0.0")
class LongMethod(config: Config) : Rule(
    config,
    "One method should have one responsibility. Long methods tend to handle many things at once. " +
        "Prefer smaller methods to make them easier to understand."
) {

    @Configuration("number of lines in a method that are allowed at maximum")
    private val allowedLines: Int by config(defaultValue = 60)

    private val functionToLinesCache = HashMap<KtNamedFunction, Int>()
    private val functionToBodyLinesCache = HashMap<KtNamedFunction, Int>()
    private val nestedFunctionTracking = IdentityHashMap<KtNamedFunction, HashSet<KtNamedFunction>>()

    override fun preVisit(root: KtFile) {
        functionToLinesCache.clear()
        functionToBodyLinesCache.clear()
        nestedFunctionTracking.clear()
    }

    override fun postVisit(root: KtFile) {
        val functionToLines = HashMap<KtNamedFunction, Int>()
        functionToLinesCache.map { (function, lines) ->
            val isNested = function.getStrictParentOfType<KtNamedFunction>() != null
            if (isNested) {
                functionToLines[function] = functionToBodyLinesCache[function] ?: 0
            } else {
                functionToLines[function] = lines
            }
        }
        for ((function, lines) in functionToLines) {
            if (lines > allowedLines) {
                report(
                    CodeSmell(
                        Entity.atName(function),
                        "The function ${function.nameAsSafeName} is too long ($lines). " +
                            "The maximum length is $allowedLines."
                    )
                )
            }
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        val parentMethods = function.getStrictParentOfType<KtNamedFunction>()
        val bodyEntity = function.bodyBlockExpression ?: function.bodyExpression
        val lines = (if (parentMethods != null) function else bodyEntity)?.linesOfCode() ?: 0
        functionToLinesCache[function] = lines
        functionToBodyLinesCache[function] = bodyEntity?.linesOfCode() ?: 0
        parentMethods?.let { nestedFunctionTracking.getOrPut(it) { HashSet() }.add(function) }
        super.visitNamedFunction(function)

        PsiTreeUtil.findChildrenOfType(function, KtNamedFunction::class.java)
            .fold(0) { acc, next -> acc + (functionToLinesCache[next] ?: 0) }
            .takeIf { it > 0 }
            ?.let { functionToLinesCache[function] = lines - it }
    }
}
