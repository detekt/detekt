package dev.detekt.rules.potentialbugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny

/**
 * Prefer passing [java.util.Locale] explicitly than using implicit default value when formatting
 * strings or performing a case conversion.
 *
 * The default locale is almost always inappropriate for machine-readable text like HTTP headers.
 * For example, if locale with tag `ar-SA-u-nu-arab` is a current default then `%d` placeholders
 * will be evaluated to a number consisting of Eastern-Arabic (non-ASCII) digits.
 * [java.util.Locale.US] is recommended for machine-readable output.
 *
 * <noncompliant>
 * String.format("Timestamp: %d", System.currentTimeMillis())
 * "Timestamp: %d".format(System.currentTimeMillis())
 *
 * </noncompliant>
 *
 * <compliant>
 * String.format(Locale.US, "Timestamp: %d", System.currentTimeMillis())
 * "Timestamp: %d".format(Locale.US, System.currentTimeMillis())
 *
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class ImplicitDefaultLocale(config: Config) :
    Rule(
        config,
        "Implicit default locale used for string processing. Consider using explicit locale."
    ),
    RequiresAnalysisApi {

    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)
        checkStringFormatting(expression)
    }

    @Suppress("ReturnCount")
    private fun checkStringFormatting(expression: KtQualifiedExpression) {
        val formatCallId = formatCallIds[expression.getCalleeExpressionIfAny()?.text] ?: return

        analyze(expression) {
            val symbol = expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol ?: return
            if (symbol.callableId != formatCallId) return
            if (symbol.valueParameters.firstOrNull()?.returnType?.symbol?.classId == localeClassId) return
            report(
                Finding(
                    Entity.from(expression),
                    "${expression.text} uses implicitly default locale for string formatting."
                )
            )
        }
    }

    companion object {
        private val formatCallIds = listOf(
            CallableId(FqName("kotlin.text"), Name.identifier("format")),
        ).associateBy { it.callableName.asString() }

        private val localeClassId = ClassId(FqName("java.util"), Name.identifier("Locale"))
    }
}
