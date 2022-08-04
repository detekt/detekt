package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.tooling.api.FunctionMatcher
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.simplePatternToRegex
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isUnit

/**
 * This rule warns on instances where a function, annotated with either `@CheckReturnValue` or `@CheckResult`,
 * returns a value but that value is not used in any way. The Kotlin compiler gives no warning for this scenario
 * normally so that's the rationale behind this rule.
 *
 * fun returnsValue() = 42
 * fun returnsNoValue() {}
 *
 * <noncompliant>
 * returnsValue()
 * </noncompliant>
 *
 * <compliant>
 * if (42 == returnsValue()) {}
 * val x = returnsValue()
 * </compliant>
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class IgnoredReturnValue(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "IgnoredReturnValue",
        Severity.Defect,
        "This call returns a value which is ignored",
        Debt.TWENTY_MINS
    )

    @Configuration("if the rule should check only annotated methods")
    private val restrictToAnnotatedMethods: Boolean by config(defaultValue = true)

    @Configuration("List of glob patterns to be used as inspection annotation")
    private val returnValueAnnotations: List<Regex> by config(listOf("*.CheckResult", "*.CheckReturnValue")) {
        it.map(String::simplePatternToRegex)
    }

    @Configuration("Annotations to skip this inspection")
    private val ignoreReturnValueAnnotations: List<Regex> by config(listOf("*.CanIgnoreReturnValue")) {
        it.map(String::simplePatternToRegex)
    }

    @Configuration(
        "List of function signatures which should be ignored by this rule. " +
            "Specifying fully-qualified function signature with name only (i.e. `java.time.LocalDate.now`) will " +
            "ignore all function calls matching the name. Specifying fully-qualified function signature with " +
            "parameters (i.e. `java.time.LocalDate.now(java.time.Clock)`) will ignore only function calls matching " +
            "the name and parameters exactly."
    )
    private val ignoreFunctionCall: List<FunctionMatcher> by config(emptyList<String>()) {
        it.map(FunctionMatcher::fromFunctionSignature)
    }

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.isUsedAsExpression(bindingContext)) return

        val resultingDescriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return
        if (resultingDescriptor.returnType?.isUnit() == true) return

        if (ignoreFunctionCall.any { it.match(resultingDescriptor) }) return

        val annotations = resultingDescriptor.annotations
        if (annotations.any { it in ignoreReturnValueAnnotations }) return
        if (restrictToAnnotatedMethods &&
            (annotations + resultingDescriptor.containingDeclaration.annotations).none { it in returnValueAnnotations }
        ) return

        val messageText = expression.calleeExpression?.text ?: expression.text
        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                message = "The call $messageText is returning a value that is ignored."
            )
        )
    }

    @Suppress("UnusedPrivateMember")
    private operator fun List<Regex>.contains(annotation: AnnotationDescriptor): Boolean {
        val fqName = annotation.fqName?.asString() ?: return false
        return any { it.matches(fqName) }
    }
}
