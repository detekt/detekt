package dev.detekt.rules.complexity

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.valuesWithReason
import dev.detekt.psi.FunctionMatcher
import dev.detekt.psi.FunctionMatcher.Companion.fromFunctionSignature
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument

/**
 * Reports function invocations which have more arguments than a certain threshold and are all not named. Calls with
 * too many arguments are more difficult to understand so a named arguments help.
 *
 * <noncompliant>
 * fun sum(a: Int, b: Int, c: Int, d: Int) {
 * }
 * sum(1, 2, 3, 4)
 * </noncompliant>
 *
 * <compliant>
 * fun sum(a: Int, b: Int, c: Int, d: Int) {
 * }
 * sum(a = 1, b = 2, c = 3, d = 4)
 * </compliant>
 */
class NamedArguments(config: Config) :
    Rule(
        config,
        "Named arguments are required for function calls with many arguments."
    ),
    RequiresAnalysisApi {

    @Configuration("The allowed number of arguments for a function.")
    private val allowedArguments: Int by config(defaultValue = 3)

    @Configuration(
        "List of fully qualified method signatures where this rule is ignored. " +
            "Methods can be defined without full signatures (i.e. `java.time.LocalDate.now`), which will report " +
            "calls of all methods with this name or with the full signature " +
            "(i.e. `java.time.LocalDate(java.time.Clock)`), which reports only calls" +
            "with the specific signature given. If you want to add an extension function like " +
            "`fun String.hello(a: Int)` you should add the receiver parameter as the first parameter like this: " +
            "`hello(kotlin.String, kotlin.Int)`. To add constructor calls you need to define them with `<init>`, " +
            "for example `java.util.Date.<init>`. To add calls involving type parameters, omit them, for example " +
            "`fun hello(args: Array<Any>)` is referred to as simply `hello(kotlin.Array)`. To add calls " +
            "involving varargs, for example `fun hello(vararg args: String)`, you need to define it like " +
            "`hello(vararg String)`. To add methods from the companion object reference the companion class, for " +
            "example with `TestClass.Companion.hello()`, even if it is marked `@JvmStatic`."
    )
    private val ignoreMethods: List<ForbiddenMethod> by config(
        valuesWithReason()
    ) { list ->
        list.map { ForbiddenMethod(fromFunctionSignature(it.value), it.reason) }
    }

    @Configuration("ignores when argument values are the same as the parameter names")
    private val ignoreArgumentsMatchingNames: Boolean by config(defaultValue = false)

    override fun visitCallExpression(expression: KtCallExpression) {
        val valueArguments = expression.valueArguments.filterNot { it is KtLambdaArgument }
        if (valueArguments.size > allowedArguments && expression.canNameArguments()) {
            val message = "This function call has ${valueArguments.size} arguments. To call a function with more " +
                "than $allowedArguments arguments you should set the name of each argument."
            report(Finding(Entity.from(expression), message))
        } else {
            super.visitCallExpression(expression)
        }
    }

    @Suppress("ReturnCount")
    private fun KtCallExpression.canNameArguments(): Boolean =
        analyze(this) {
            val functionCall = resolveToCall()?.singleFunctionCallOrNull() ?: return false
            if (ignoreMethods.any { it.value.match(functionCall.symbol) }) return false
            if (!functionCall.symbol.hasStableParameterNames) return false

            val unnamedArguments = valueArguments.mapNotNull { argument ->
                if (argument.isNamed() || argument is KtLambdaArgument) return@mapNotNull null
                val parameter = functionCall.argumentMapping[argument.getArgumentExpression()] ?: return@mapNotNull null
                if (ignoreArgumentsMatchingNames &&
                    parameter.name.asString() == argument.getArgumentExpression()?.text
                ) {
                    null
                } else {
                    argument to parameter
                }
            }

            unnamedArguments.isNotEmpty() &&
                unnamedArguments.count { (argument, _) -> argument.isSpread } <= 1 &&
                unnamedArguments.all { (argument, parameter) -> argument.isSpread || !parameter.symbol.isVararg }
        }

    internal data class ForbiddenMethod(val value: FunctionMatcher, val reason: String?)
}
