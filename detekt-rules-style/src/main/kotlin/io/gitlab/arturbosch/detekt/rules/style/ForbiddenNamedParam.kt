package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.psi.FunctionMatcher.Companion.fromFunctionSignature
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.valuesWithReason
import io.gitlab.arturbosch.detekt.rules.style.ForbiddenMethodCall.ForbiddenMethod
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.overriddenTreeUniqueAsSequence

/**
 * Reports all usages of method, or constructor calls for which using named parameters are forbidden.
 * This rule can be useful for some method/constructor where using the parameter
 * name doesn't give add any value to it and only makes the code more verbose
 *
 * This rule allows to set a list of forbidden [methods] or constructors. These can be used to
 * discourage the use named parameters.
 *
 * <noncompliant>
 * fun foo() {
 *     // `id =` here adds no value
 *     painterResource(id = R.drawable.ic_close)
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     painterResource(R.drawable.ic_close)
 * }
 * </compliant>
 */
class ForbiddenNamedParam(config: Config) :
    Rule(
        config,
        "Mark the methods/constructors where using named param is forbidden."
    ),
    RequiresTypeResolution {
    @Configuration(
        "List of fully qualified method signatures for which are named param is forbidden. " +
            "Methods can be defined without full signature (i.e. `java.time.LocalDate.now`) which will report " +
            "calls of all methods with this name or with full signature " +
            "(i.e. `java.time.LocalDate(java.time.Clock)`) which would report only call " +
            "with this concrete signature. If you want to add an extension function like " +
            "`fun String.hello(a: Int)` you should add the receiver parameter as the first parameter like this: " +
            "`hello(kotlin.String, kotlin.Int)`. To add constructor calls you need to define them with `<init>`, " +
            "for example `java.util.Date.<init>`. To add calls involving type parameters, omit them, for example " +
            "`fun hello(args: Array<Any>)` is referred to as simply `hello(kotlin.Array)` (also the signature for " +
            "vararg parameters). To add methods from the companion object reference the Companion class, for " +
            "example as `TestClass.Companion.hello()` (even if it is marked `@JvmStatic`)."
    )
    private val methods: List<ForbiddenMethod> by config(
        valuesWithReason()
    ) { list ->
        list.map { ForbiddenMethod(fromFunctionSignature(it.value), it.reason) }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        check(expression)
    }

    private fun check(expression: KtCallExpression) {
        if (expression.valueArguments.none { it.isNamed() }) return
        val descriptors: Set<CallableDescriptor> =
            expression.getResolvedCall(bindingContext)?.resultingDescriptor?.overriddenTreeUniqueAsSequence(
                true
            )?.toSet() ?: return

        for (descriptor in descriptors) {
            methods.find { it.value.match(descriptor) }?.let { matchingMethod ->
                val message = if (matchingMethod.reason != null) {
                    "The method `${matchingMethod.value}` has been forbidden from using named " +
                        "param: ${matchingMethod.reason}"
                } else {
                    "The method `${matchingMethod.value}` has been forbidden from using named " +
                        "param in the detekt config."
                }
                report(CodeSmell(Entity.from(expression), message))
            }
        }
    }
}
