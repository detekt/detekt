package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.psi.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.configWithFallback
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

/**
 * Reports functions and constructors which have more parameters than a certain threshold.
 */
@Suppress("ViolatesTypeResolutionRequirements")
@ActiveByDefault(since = "1.0.0")
class LongParameterList(config: Config) :
    Rule(
        config,
        "The more parameters a function has the more complex it is. Long parameter lists are often " +
            "used to control complex algorithms and violate the Single Responsibility Principle. " +
            "Prefer functions with short parameter lists."
    ),
    RequiresTypeResolution {
    @Deprecated("Use `allowedFunctionParameters` and `allowedConstructorParameters` instead")
    @Configuration("number of parameters required to trigger the rule")
    private val threshold: Int by config(DEFAULT_ALLOWED_FUNCTION_PARAMETERS)

    @Suppress("DEPRECATION")
    @Configuration("number of function parameters required to trigger the rule")
    private val allowedFunctionParameters: Int by configWithFallback(::threshold, DEFAULT_ALLOWED_FUNCTION_PARAMETERS)

    @Suppress("DEPRECATION")
    @Configuration("number of constructor parameters required to trigger the rule")
    private val allowedConstructorParameters: Int
        by configWithFallback(::threshold, DEFAULT_ALLOWED_CONSTRUCTOR_PARAMETERS)

    @Configuration("ignore parameters that have a default value")
    private val ignoreDefaultParameters: Boolean by config(false)

    @Configuration("ignore long constructor parameters list for data classes")
    private val ignoreDataClasses: Boolean by config(true)

    @Configuration(
        "ignore the annotated parameters for the count (e.g. `fun foo(@Value bar: Int)` would not be counted"
    )
    private val ignoreAnnotatedParameter: List<Regex> by config(emptyList<String>()) { list ->
        list.map { it.replace(".", "\\.").replace("*", ".*").toRegex() }
    }

    private lateinit var annotationExcluder: AnnotationExcluder

    override fun visitKtFile(file: KtFile) {
        annotationExcluder = AnnotationExcluder(file, ignoreAnnotatedParameter, bindingContext)
        super.visitKtFile(file)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        checkLongParameterList(function, allowedFunctionParameters, "function ${function.nameAsSafeName}")
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        validateConstructor(constructor)
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        validateConstructor(constructor)
    }

    private fun KtAnnotated.isIgnored(): Boolean = annotationExcluder.shouldExclude(annotationEntries)

    private fun validateConstructor(constructor: KtConstructor<*>) {
        val owner = constructor.getContainingClassOrObject()
        if (owner is KtClass && owner.isDataClassOrIgnored()) {
            return
        }
        checkLongParameterList(constructor, allowedConstructorParameters, "constructor")
    }

    private fun KtClass.isDataClassOrIgnored() = ignoreDataClasses && isData()

    private fun checkLongParameterList(function: KtFunction, maximumAllowedParameter: Int, identifier: String) {
        if (function.isOverride()) return
        val parameterList = function.valueParameterList ?: return
        val parameterNumber = parameterList.parameterCount()

        if (parameterNumber > maximumAllowedParameter) {
            val parameterPrint = function.valueParameters.joinToString(separator = ", ") {
                it.nameAsSafeName.identifier + ": " + it.typeReference?.text
            }

            report(
                ThresholdedCodeSmell(
                    Entity.from(parameterList),
                    Metric(parameterNumber, maximumAllowedParameter),
                    "The $identifier($parameterPrint) has too many parameters. " +
                        "The current maximum allowed parameters are $maximumAllowedParameter."
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
        private const val DEFAULT_ALLOWED_FUNCTION_PARAMETERS = 5
        private const val DEFAULT_ALLOWED_CONSTRUCTOR_PARAMETERS = 6
    }
}
