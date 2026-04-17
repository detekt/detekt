package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Detects when lambdas passed to `analyze {}` return types implementing `KaSession` or `KaLifetimeOwner`.
 *
 * These objects are lifetime-scoped to the analysis block and will cause runtime errors if they
 * escape. Return a plain data class, primitive, or `KaSymbolPointer` instead.
 *
 * <noncompliant>
 * val type = analyze(element) {
 *     element.expressionType // returns KaType which implements KaLifetimeOwner
 * }
 * </noncompliant>
 *
 * <compliant>
 * val typeName = analyze(element) {
 *     element.expressionType?.toString() // returns a String instead
 * }
 * </compliant>
 */
class AvoidLeakingAnalysisApiTypesFromSessions(config: Config = Config.empty) :
    Rule(
        config,
        "Lambda passed to `analyze {}` returns a lifetime-scoped type." +
            " These objects are scoped to the analysis block and will cause errors outside it." +
            " Return a plain data class, primitive, or `KaSymbolPointer` instead.",
    ),
    RequiresAnalysisApi {

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)

        val lambdaArgument = lambdaExpression.getParentOfType<KtLambdaArgument>(strict = true) ?: return
        val callExpression = lambdaArgument.getParentOfType<KtCallExpression>(strict = true) ?: return

        analyze(lambdaExpression) {
            val resolvedCall = callExpression.resolveToCall()?.singleFunctionCallOrNull() ?: return
            if (resolvedCall.symbol.callableId != analyzeCallableId) return
            val returnType = callExpression.expressionType ?: return

            if (returnType.usesBannedType()) {
                report(
                    Finding(
                        entity = Entity.from(lambdaExpression),
                        message = "analyze {} returns lifetime-scoped type '$returnType'. " +
                            "Return a plain data class or primitive instead.",
                    ),
                )
            }
        }
    }

    @Suppress("ReturnCount")
    context(session: KaSession)
    private fun KaType.usesBannedType(): Boolean {
        val classType = this as? KaClassType ?: return false
        if (classType.classId in bannedReturnTypes) return true
        with(session) {
            if (classType.allSupertypes.any { (it as? KaClassType)?.classId in bannedReturnTypes }) return true
        }
        if (classType.classId in allowedWrapperTypes) return false
        return classType.typeArguments.any { arg -> arg.type?.usesBannedType() ?: false }
    }

    companion object {
        private val analyzeCallableId = CallableId(
            FqName("org.jetbrains.kotlin.analysis.api"),
            Name.identifier("analyze"),
        )

        private val bannedReturnTypes = setOf(
            ClassId(FqName("org.jetbrains.kotlin.analysis.api"), FqName("KaSession"), false),
            ClassId(FqName("org.jetbrains.kotlin.analysis.api.lifetime"), FqName("KaLifetimeOwner"), false),
        )

        private val allowedWrapperTypes = setOf(
            ClassId(
                FqName("org.jetbrains.kotlin.analysis.api.symbols.pointers"),
                FqName("KaSymbolPointer"),
                false,
            ),
        )
    }
}
