package dev.detekt.rules.potentialbugs

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.simplePatternToRegex
import dev.detekt.psi.FunctionMatcher
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaDeclarationSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolOrigin
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaFunctionType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.load.java.JavaClassFinderImpl
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

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
    RequiresAnalysisApi {

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

    @Suppress("ComplexCondition", "ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        analyze(expression) {
            val symbol = expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol ?: return
            val returnType = symbol.returnType
            if (returnType.isUnitType || returnType.isNothingType) return

            if (ignoreFunctionCall.any { it.match(symbol) }) return

            if (isUsedAsExpression(expression, returnType)) return

            val containingDeclaration = symbol.containingDeclaration
            val annotations = buildList {
                addAll(symbol.annotations())
                addAll(containingDeclaration.annotations())
                addAll(containingDeclaration.javaPackageAnnotations(expression.resolveScope, expression.project))
            }

            if (annotations.any { it in ignoreReturnValueAnnotations }) return

            if (restrictToConfig &&
                returnType !in returnValueTypes &&
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
    }

    private fun KaDeclarationSymbol?.annotations(): List<ClassId> =
        this?.annotations?.mapNotNull { it.classId }.orEmpty()

    @Suppress("ReturnCount")
    private fun KaDeclarationSymbol?.javaPackageAnnotations(scope: GlobalSearchScope, project: Project): List<ClassId> {
        val origin = (this as? KaClassSymbol)?.origin
        if (origin != KaSymbolOrigin.JAVA_SOURCE && origin != KaSymbolOrigin.JAVA_LIBRARY) return emptyList()
        val packageFqName = this.classId?.packageFqName ?: return emptyList()
        val javaClassFinder = JavaClassFinderImpl().apply {
            setScope(scope)
            setProjectInstance(project)
        }
        return javaClassFinder.findPackage(packageFqName)?.annotations?.mapNotNull { it.classId }.orEmpty()
    }

    private fun KaSession.isUsedAsExpression(call: KtCallExpression, returnType: KaType): Boolean {
        if (returnType is KaFunctionType &&
            call.getStrictParentOfType<KtCallExpression>()?.calleeExpression == KtPsiUtil.safeDeparenthesize(call)
        ) {
            return true
        }

        if (!call.isUsedAsExpression) return false

        val lambda = call.getStrictParentOfType<KtFunctionLiteral>()?.parent as? KtLambdaExpression
        if (lambda != null && call.isLambdaResult(lambda)) {
            val parentCall = (lambda.parent as? KtValueArgument)?.getStrictParentOfType<KtCallExpression>()
            val isLambdaResultScopeFunction = parentCall?.isCalling(lambdaResultScopeFunctions) == true
            val isUsedAsExpression = parentCall?.isUsedAsExpression == true
            if (isLambdaResultScopeFunction && !isUsedAsExpression) return false
        }

        return true
    }

    context(session: KaSession)
    private fun KtExpression.isLambdaResult(lambda: KtLambdaExpression): Boolean {
        val statement = getQualifiedExpressionForSelectorOrThis().let {
            it.getStrictParentOfType<KtReturnExpression>() ?: it
        }
        return when (statement) {
            is KtReturnExpression -> {
                with(session) {
                    val symbol = lambda.functionLiteral.symbol
                    val label = (statement as? KtExpressionWithLabel)?.getTargetLabel()
                    label?.mainReference?.resolveToSymbol() == symbol
                }
            }

            else -> {
                val lastStatement = lambda.functionLiteral.bodyBlockExpression?.children?.lastOrNull()
                statement == lastStatement
            }
        }
    }

    private operator fun List<Regex>.contains(type: KaType?) = contains((type as? KaClassType)?.classId)

    private operator fun List<Regex>.contains(classId: ClassId?): Boolean {
        val name = classId?.asSingleFqName()?.asString() ?: return false
        return any { it.matches(name) }
    }

    companion object {
        private val lambdaResultScopeFunctions = listOf("with", "run", "let").map {
            CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier(it))
        }
    }
}
