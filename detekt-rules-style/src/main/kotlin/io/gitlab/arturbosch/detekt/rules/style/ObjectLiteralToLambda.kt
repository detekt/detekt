package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.singleCallOrNull
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

/**
 * An anonymous object that does nothing other than the implementation of a single method
 * can be used as a lambda.
 *
 * See [SAM conversions](https://kotlinlang.org/docs/java-interop.html#sam-conversions),
 * [Functional (SAM) interfaces](https://kotlinlang.org/docs/fun-interfaces.html)
 *
 * <noncompliant>
 * object : Foo {
 *     override fun bar() {
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * Foo {
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class ObjectLiteralToLambda(config: Config) :
    Rule(
        config,
        "Report object literals that can be changed to lambdas."
    ),
    RequiresAnalysisApi {

    context(session: KaSession)
    private fun KtExpression.containsThisReference(objectSymbol: KaClassSymbol) = with(session) {
        anyDescendantOfType<KtThisExpression> {
            it.expressionType?.symbol == objectSymbol
        }
    }

    context(session: KaSession)
    private fun KtExpression.containsOwnMethodCall(objectSymbol: KaClassSymbol) = with(session) {
        anyDescendantOfType<KtExpression> { expr ->
            val symbol = expr.resolveToCall()?.singleCallOrNull<KaCallableMemberCall<*, *>>()?.partiallyAppliedSymbol
            listOfNotNull(symbol?.dispatchReceiver, symbol?.extensionReceiver).any { it.type.symbol == objectSymbol }
        }
    }

    context(session: KaSession)
    private fun KtExpression.containsMethodOf(declaration: KtObjectDeclaration): Boolean {
        with(session) {
            val objectSymbol = declaration.symbol
            return containsThisReference(objectSymbol) || containsOwnMethodCall(objectSymbol)
        }
    }

    context(session: KaSession)
    private fun KtObjectDeclaration.hasConvertibleMethod(): Boolean {
        val singleNamedMethod = declarations.singleOrNull() as? KtNamedFunction
        val functionBody = singleNamedMethod?.bodyExpression ?: return false
        return singleNamedMethod.isOverride() && !functionBody.containsMethodOf(this)
    }

    override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
        super.visitObjectLiteralExpression(expression)

        val declaration = expression.objectDeclaration
        analyze(expression) {
            if (declaration.name == null &&
                expression.symbol.superTypes.singleOrNull()?.isFunctionalInterface == true &&
                declaration.hasConvertibleMethod()
            ) {
                report(Finding(Entity.from(expression), description))
            }
        }
    }
}
