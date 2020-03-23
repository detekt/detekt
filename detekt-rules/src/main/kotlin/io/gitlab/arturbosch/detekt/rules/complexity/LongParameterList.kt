package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

/**
 * Reports functions and constructors which have more parameters than a certain threshold.
 *
 * @configuration threshold - number of parameters required to trigger the rule (default: `6`)
 * (deprecated: "Use `functionThreshold` and `constructorThreshold` instead")
 * @configuration functionThreshold - number of function parameters required to trigger the rule (default: `6`)
 * @configuration constructorThreshold - number of constructor parameters required to trigger the rule (default: `7`)
 * @configuration ignoreDefaultParameters - ignore parameters that have a default value (default: `false`)
 * @configuration ignoreDataClasses - ignore long constructor parameters list for data classes (default: `true`)
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

    override fun visitNamedFunction(function: KtNamedFunction) {
        validateFunction(function, functionThreshold)
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        validateConstructor(constructor)
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        validateConstructor(constructor)
    }

    private fun validateConstructor(constructor: KtConstructor<*>) {
        val owner = constructor.getContainingClassOrObject()
        val isDataClass = owner is KtClass && owner.isData()
        if (!ignoreDataClasses || !isDataClass) {
            validateFunction(constructor, constructorThreshold)
        }
    }

    private fun validateFunction(function: KtFunction, threshold: Int) {
        if (function.isOverride()) return
        val parameterList = function.valueParameterList
        val parameters = parameterList?.parameterCount()

        if (parameters != null && parameters >= threshold) {
            report(ThresholdedCodeSmell(issue,
                    Entity.from(parameterList),
                    Metric("SIZE", parameters, threshold),
                    "The function ${function.nameAsSafeName} has too many parameters. The current threshold" +
                            " is set to $threshold."))
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

        const val DEFAULT_FUNCTION_THRESHOLD = 6
        const val DEFAULT_CONSTRUCTOR_THRESHOLD = 7
    }
}
