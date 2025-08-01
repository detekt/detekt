package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresFullAnalysis
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.simplePatternToRegex
import dev.detekt.psi.FunctionMatcher
import dev.detekt.psi.fqNameOrNull
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.findPackage
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext.FUNCTION
import org.jetbrains.kotlin.resolve.bindingContextUtil.getTargetFunctionDescriptor
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isNothing
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
@ActiveByDefault(since = "1.21.0")
class IgnoredReturnValue(config: Config) :
    Rule(
        config,
        "This call returns a value which is ignored"
    ),
    RequiresFullAnalysis {

    @Configuration("If the rule should check only methods matching to configuration, or all methods")
    private val restrictToConfig: Boolean by config(defaultValue = true)

    @Configuration("List of glob patterns to be used as inspection annotation")
    private val returnValueAnnotations: List<Regex> by config(
        listOf(
            "CheckResult",
            "*.CheckResult",
            "CheckReturnValue",
            "*.CheckReturnValue"
        )
    ) {
        it.map(String::simplePatternToRegex)
    }

    @Configuration("Annotations to skip this inspection")
    private val ignoreReturnValueAnnotations: List<Regex> by config(
        listOf(
            "CanIgnoreReturnValue",
            "*.CanIgnoreReturnValue"
        )
    ) {
        it.map(String::simplePatternToRegex)
    }

    @Configuration("List of return types that should not be ignored")
    private val returnValueTypes: List<Regex> by config(
        listOf(
            "kotlin.Function*",
            "kotlin.sequences.Sequence",
            "kotlinx.coroutines.flow.*Flow",
            "java.util.stream.*Stream",
        ),
    ) { it.map(String::simplePatternToRegex) }

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

    @Suppress("ComplexCondition")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (isUsedAsExpression(expression)) return

        val resultingDescriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return
        if (resultingDescriptor.returnType?.isUnit() == true) return
        if (resultingDescriptor.returnType?.isNothing() == true) return

        if (ignoreFunctionCall.any { it.match(resultingDescriptor) }) return

        val annotations = buildList {
            addAll(resultingDescriptor.annotations)
            addAll(resultingDescriptor.findPackage().annotations)
            addAll(resultingDescriptor.containingDeclaration.annotations)
        }
        if (annotations.any { it in ignoreReturnValueAnnotations }) return
        if (restrictToConfig &&
            resultingDescriptor.returnType !in returnValueTypes &&
            annotations.none { it in returnValueAnnotations }
        ) {
            return
        }

        val messageText = expression.calleeExpression?.text ?: expression.text
        report(
            Finding(
                Entity.from(expression),
                message = "The call $messageText is returning a value that is ignored."
            )
        )
    }

    private fun isUsedAsExpression(call: KtCallExpression): Boolean {
        if (!call.isUsedAsExpression(bindingContext)) return false

        val lambda = call.getStrictParentOfType<KtFunctionLiteral>()?.parent as? KtLambdaExpression
        if (lambda != null && call.isLambdaResult(lambda)) {
            val parentCall = (lambda.parent as? KtValueArgument)?.getStrictParentOfType<KtCallExpression>()
            val isLambdaResultScopeFunction = parentCall?.isCalling(lambdaResultScopeFunctions, bindingContext) == true
            val isUsedAsExpression = parentCall?.isUsedAsExpression(bindingContext) == true
            if (isLambdaResultScopeFunction && !isUsedAsExpression) return false
        }

        return true
    }

    private fun KtExpression.isLambdaResult(lambda: KtLambdaExpression): Boolean {
        val statement = getQualifiedExpressionForSelectorOrThis().let {
            it.getStrictParentOfType<KtReturnExpression>() ?: it
        }
        return when (statement) {
            is KtReturnExpression -> {
                val lambdaDescriptor = bindingContext[FUNCTION, lambda.functionLiteral]
                statement.getTargetFunctionDescriptor(bindingContext) == lambdaDescriptor
            }

            else -> {
                val lastStatement = lambda.functionLiteral.bodyBlockExpression?.children?.lastOrNull()
                statement == lastStatement
            }
        }
    }

    private operator fun List<Regex>.contains(type: KotlinType?) = contains(type?.fqNameOrNull())

    private operator fun List<Regex>.contains(annotation: AnnotationDescriptor) = contains(annotation.fqName)

    private operator fun List<Regex>.contains(fqName: FqName?): Boolean {
        val name = fqName?.asString() ?: return false
        return any { it.matches(name) }
    }

    companion object {
        private val lambdaResultScopeFunctions = listOf("with", "run", "let").map { FqName("kotlin.$it") }
    }
}
