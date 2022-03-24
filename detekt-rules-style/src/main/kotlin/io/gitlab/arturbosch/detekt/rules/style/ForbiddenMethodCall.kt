package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.tooling.api.FunctionMatcher
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

/**
 * This rule allows to set a list of forbidden methods. This can be used to discourage the use of unstable, experimental
 * or deprecated methods, especially for methods imported from external libraries.
 * Detekt will then report all method invocations that are forbidden.
 *
 * <noncompliant>
 * import java.lang.System
 * fun main() {
 *    System.gc()
 * }
 * </noncompliant>
 *
 */
@RequiresTypeResolution
class ForbiddenMethodCall(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Mark forbidden methods. A forbidden method could be an invocation of an unstable / experimental " +
            "method and hence you might want to mark it as forbidden in order to get warned about the usage.",
        Debt.TEN_MINS
    )

    @Configuration(
        "List of fully qualified method signatures which are forbidden. " +
            "Methods can be defined without full signature (i.e. `java.time.LocalDate.now`) which will report " +
            "calls of all methods with this name or with full signature " +
            "(i.e. `java.time.LocalDate(java.time.Clock)`) which would report only call " +
            "with this concrete signature. If you want to forbid an extension function like" +
            "`fun String.hello(a: Int)` you should add the receiver parameter as the first parameter like this: " +
            "`hello(kotlin.String, kotlin.Int)`"
    )
    private val methods: List<FunctionMatcher> by config(
        listOf(
            "kotlin.io.print",
            "kotlin.io.println",
        )
    ) { it.map(FunctionMatcher::fromFunctionSignature) }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        check(expression)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        check(expression.operationReference)
    }

    override fun visitPrefixExpression(expression: KtPrefixExpression) {
        super.visitPrefixExpression(expression)
        check(expression.operationReference)
    }

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        super.visitPostfixExpression(expression)
        check(expression.operationReference)
    }

    private fun check(expression: KtExpression) {
        if (bindingContext == BindingContext.EMPTY) return

        val descriptors = expression.getResolvedCall(bindingContext)?.resultingDescriptor?.let {
            listOf(it) + it.overriddenDescriptors
        } ?: return

        for (descriptor in descriptors) {
            methods.find { it.match(descriptor) }?.let { functionMatcher ->
                val message = "The method $functionMatcher has been forbidden in the Detekt config."
                report(CodeSmell(issue, Entity.from(expression), message))
            }
        }
    }
}
