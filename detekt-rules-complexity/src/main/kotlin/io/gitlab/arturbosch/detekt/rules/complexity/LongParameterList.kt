package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

/**
 * Reports functions and constructors which have more parameters than a certain threshold.
 *
 * @configuration threshold - number of parameters required to trigger the rule (default: `6`)
 * (deprecated: "Use `functionThreshold` and `constructorThreshold` instead")
 * @configuration functionThreshold - number of function parameters required to trigger the rule (default: `6`)
 * @configuration constructorThreshold - number of constructor parameters required to trigger the rule (default: `7`)
 * @configuration ignoreDefaultParameters - ignore parameters that have a default value (default: `false`)
 * @configuration ignoreDataClasses - ignore long constructor parameters list for data classes (default: `true`)
 * @configuration ignoreAnnotated - ignore long parameters list for constructors or functions in the context of these
 * annotation class names (default: `[]`); (e.g. ['Inject', 'Module', 'Suppress']);
 * the most common case is for dependency injection where constructors are annotated with @Inject.
 *
 * @active since v1.0.0
 */
class LongParameterList(
    config: Config = Config.empty
) : Rule(config) {

    override val issue = Issue("LongParameterList",
            Severity.Maintainability,
            "The more parameters a function has the more complex it is. Long parameter lists are often " +
                    "used to control complex algorithms and violate the Single Responsibility Principle. " +
                    "Prefer functions with short parameter lists.",
            Debt.TWENTY_MINS)

    private val functionThreshold: Int =
        valueOrDefault(FUNCTION_THRESHOLD, valueOrDefault(THRESHOLD, DEFAULT_FUNCTION_THRESHOLD))

    private val constructorThreshold: Int =
        valueOrDefault(CONSTRUCTOR_THRESHOLD, valueOrDefault(THRESHOLD, DEFAULT_CONSTRUCTOR_THRESHOLD))

    private val ignoreDefaultParameters = valueOrDefault(IGNORE_DEFAULT_PARAMETERS, false)

    private val ignoreDataClasses = valueOrDefault(IGNORE_DATA_CLASSES, true)

    private val ignoreAnnotated = valueOrDefaultCommaSeparated(IGNORE_ANNOTATED, emptyList())
        .map { it.removePrefix("*").removeSuffix("*") }

    private lateinit var annotationExcluder: AnnotationExcluder

    override fun visitKtFile(file: KtFile) {
        annotationExcluder = AnnotationExcluder(file, ignoreAnnotated)
        super.visitKtFile(file)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        val owner = function.containingClassOrObject
        if (owner is KtClass && owner.isIgnored()) {
            return
        }
        checkLongParameterList(function, functionThreshold, "function ${function.nameAsSafeName}")
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        validateConstructor(constructor)
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        validateConstructor(constructor)
    }

    private fun KtAnnotated.isIgnored(): Boolean {
        return annotationExcluder.shouldExclude(annotationEntries)
    }

    private fun validateConstructor(constructor: KtConstructor<*>) {
        val owner = constructor.getContainingClassOrObject()
        if (owner is KtClass && owner.isDataClassOrIgnored()) {
            return
        }
        checkLongParameterList(constructor, constructorThreshold, "constructor")
    }

    private fun KtClass.isDataClassOrIgnored() = isIgnored() || ignoreDataClasses && isData()

    private fun checkLongParameterList(function: KtFunction, threshold: Int, identifier: String) {
        if (function.isOverride() || function.isIgnored() || function.containingKtFile.isIgnored()) return
        val parameterList = function.valueParameterList ?: return
        val parameterNumber = parameterList.parameterCount()

        if (parameterNumber >= threshold) {
            val parameterPrint = function.valueParameters.joinToString(separator = ", ") {
                    it.nameAsSafeName.identifier + ": " + it.typeReference?.text
            }

            report(ThresholdedCodeSmell(issue,
                    Entity.from(parameterList),
                    Metric("SIZE", parameterNumber, threshold),
                    "The $identifier($parameterPrint) has too many parameters. " +
                            "The current threshold is set to $threshold."))
        }
    }

    private fun KtParameterList.parameterCount(): Int {
        return if (ignoreDefaultParameters) {
            parameters.filter { !it.hasDefaultValue() }.size
        } else {
            parameters.size
        }
    }

    companion object {
        const val THRESHOLD = "threshold"
        const val FUNCTION_THRESHOLD = "functionThreshold"
        const val CONSTRUCTOR_THRESHOLD = "constructorThreshold"
        const val IGNORE_DEFAULT_PARAMETERS = "ignoreDefaultParameters"
        const val IGNORE_DATA_CLASSES = "ignoreDataClasses"
        const val IGNORE_ANNOTATED = "ignoreAnnotated"

        const val DEFAULT_FUNCTION_THRESHOLD = 6
        const val DEFAULT_CONSTRUCTOR_THRESHOLD = 7
    }
}
