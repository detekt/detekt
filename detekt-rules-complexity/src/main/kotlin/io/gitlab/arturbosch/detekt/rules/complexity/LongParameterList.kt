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
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithFallback
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
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
 */
@ActiveByDefault(since = "1.0.0")
class LongParameterList(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        "LongParameterList",
        Severity.Maintainability,
        "The more parameters a function has the more complex it is. Long parameter lists are often " +
            "used to control complex algorithms and violate the Single Responsibility Principle. " +
            "Prefer functions with short parameter lists.",
        Debt.TWENTY_MINS
    )

    @Suppress("unused")
    @Deprecated("Use `functionThreshold` and `constructorThreshold` instead")
    @Configuration("number of parameters required to trigger the rule")
    private val threshold: Int by config(DEFAULT_FUNCTION_THRESHOLD)

    @OptIn(UnstableApi::class)
    @Configuration("number of function parameters required to trigger the rule")
    private val functionThreshold: Int by configWithFallback(
        fallbackPropertyName = "threshold",
        defaultValue = DEFAULT_FUNCTION_THRESHOLD
    )

    @OptIn(UnstableApi::class)
    @Configuration("number of constructor parameters required to trigger the rule")
    private val constructorThreshold: Int by configWithFallback(
        fallbackPropertyName = "threshold",
        defaultValue = DEFAULT_CONSTRUCTOR_THRESHOLD
    )

    @Configuration("ignore parameters that have a default value")
    private val ignoreDefaultParameters: Boolean by config(defaultValue = false)

    @Configuration("ignore long constructor parameters list for data classes")
    private val ignoreDataClasses: Boolean by config(defaultValue = true)

    @Configuration(
        "ignore long parameters list for constructors, functions or their parameters in the " +
            "context of these annotation class names; (e.g. ['Inject', 'Module', 'Suppress', 'Value']); " +
            "the most common cases are for dependency injection where constructors are annotated with `@Inject` " +
            "or parameters are annotated with `@Value` and should not be counted for the rule to trigger"
    )
    private val ignoreAnnotated: List<String> by config(emptyList<String>()) { list ->
        list.map { it.removePrefix("*").removeSuffix("*") }
    }

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

            report(
                ThresholdedCodeSmell(
                    issue,
                    Entity.from(parameterList),
                    Metric("SIZE", parameterNumber, threshold),
                    "The $identifier($parameterPrint) has too many parameters. " +
                        "The current threshold is set to $threshold."
                )
            )
        }
    }

    private fun KtParameterList.parameterCount(): Int {
        val preFilteredParameters = parameters.filter { !it.isIgnored() }
        return if (ignoreDefaultParameters) {
            preFilteredParameters.count { !it.hasDefaultValue() }
        } else {
            preFilteredParameters.size
        }
    }

    companion object {
        private const val DEFAULT_FUNCTION_THRESHOLD = 6
        private const val DEFAULT_CONSTRUCTOR_THRESHOLD = 7
    }
}
