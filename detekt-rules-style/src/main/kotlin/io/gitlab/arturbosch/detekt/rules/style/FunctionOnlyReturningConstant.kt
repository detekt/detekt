package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * A function that only returns a single constant can be misleading. Instead prefer to define the constant directly
 * as a `const val`.
 *
 * <noncompliant>
 * fun functionReturningConstantString() = "1"
 * </noncompliant>
 *
 * <compliant>
 * const val constantString = "1"
 * </compliant>
 *
 * @configuration ignoreOverridableFunction - if overriden functions should be ignored (default: `true`)
 * @configuration excludedFunctions - excluded functions (default: `'describeContents'`)
 * @configuration excludeAnnotatedFunction - allows to provide a list of annotations that disable this check
 * (default: `['dagger.Provides']`)
 *
 * @active since v1.2.0
 */
class FunctionOnlyReturningConstant(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
        "A function that only returns a constant is misleading. " +
            "Consider declaring a constant instead",
        Debt.TEN_MINS)

    private val ignoreOverridableFunction = valueOrDefault(IGNORE_OVERRIDABLE_FUNCTION, true)
    private val excludedFunctions = SplitPattern(valueOrDefault(EXCLUDED_FUNCTIONS, ""))
    private val excludeAnnotatedFunctions = valueOrDefaultCommaSeparated(
            EXCLUDE_ANNOTATED_FUNCTION, listOf("dagger.Provides"))
        .map { it.removePrefix("*").removeSuffix("*") }
    private lateinit var annotationExcluder: AnnotationExcluder

    override fun visit(root: KtFile) {
        annotationExcluder = AnnotationExcluder(root, excludeAnnotatedFunctions)
        super.visit(root)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (checkOverridableFunction(function) &&
            isNotExcluded(function) &&
            isReturningAConstant(function)) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    "${function.nameAsSafeName} is returning a constant. Prefer declaring a constant instead."
                ))
        }
        super.visitNamedFunction(function)
    }

    private fun checkOverridableFunction(function: KtNamedFunction): Boolean =
        if (ignoreOverridableFunction) {
            !function.isOverride() && !function.isOpen() && !checkContainingInterface(function)
        } else {
            true
        }

    private fun checkContainingInterface(function: KtNamedFunction): Boolean {
        val containingClass = function.containingClass()
        return containingClass != null && containingClass.isInterface()
    }

    private fun isNotExcluded(function: KtNamedFunction) =
        !excludedFunctions.contains(function.name) && !annotationExcluder.shouldExclude(function.annotationEntries)

    private fun isReturningAConstant(function: KtNamedFunction) =
        isConstantExpression(function.bodyExpression) || returnsConstant(function)

    private fun isConstantExpression(expression: KtExpression?): Boolean {
        if (expression is KtConstantExpression) {
            return true
        }
        return expression is KtStringTemplateExpression && !expression.hasInterpolation()
    }

    private fun returnsConstant(function: KtNamedFunction): Boolean {
        val returnExpression = function.bodyExpression?.children?.singleOrNull() as? KtReturnExpression
        return isConstantExpression(returnExpression?.returnedExpression)
    }

    companion object {
        const val IGNORE_OVERRIDABLE_FUNCTION = "ignoreOverridableFunction"
        const val EXCLUDED_FUNCTIONS = "excludedFunctions"
        const val EXCLUDE_ANNOTATED_FUNCTION = "excludeAnnotatedFunction"
    }
}
