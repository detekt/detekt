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
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
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

            // Don't flag if the format string only contains locale-independent specifiers
            val formatString = getFormatString(expression)
            if (formatString != null && hasOnlyLocaleIndependentSpecifiers(formatString)) return

            report(
                Finding(
                    Entity.from(expression),
                    "${expression.text} uses implicitly default locale for string formatting."
                )
            )
        }
    }

    private fun getFormatString(expression: KtQualifiedExpression): String? {
        val receiver = expression.receiverExpression

        // Case 1: "format".format(args) - receiver is the format string
        if (receiver is KtStringTemplateExpression && !receiver.hasInterpolation()) {
            return receiver.entries.joinToString("") { it.text }
        }

        // Case 2: String.format("format", args) - format string is the first argument
        val callExpression = expression.selectorExpression as? KtCallExpression
        val firstArg = callExpression?.valueArguments?.firstOrNull()?.getArgumentExpression()
        return when {
            firstArg is KtStringTemplateExpression && !firstArg.hasInterpolation() ->
                firstArg.entries.joinToString("") { it.text }

            else -> null
        }
    }

    private fun hasOnlyLocaleIndependentSpecifiers(formatString: String): Boolean {
        // Java format specifier pattern: %[argument_index$][flags][width][.precision]conversion
        // or %[argument_index$][flags][width][.precision]t/T<date/time conversion>
        val specifierPattern = Regex("""%(\d+\$)?[-#+ 0,(]*\d*(\.\d+)?([tT][a-zA-Z]|[a-zA-Z%])""")
        val conversions = specifierPattern.findAll(formatString).map { it.groupValues[CONVERSION_GROUP_INDEX] }.toList()

        // If no format specifiers found, no locale dependency
        if (conversions.isEmpty()) return true

        // Locale-independent conversions according to Java Formatter documentation:
        // - x, X: hexadecimal integer (no localization applied)
        // - o: octal integer (no localization applied)
        // - a, A: hexadecimal floating-point (no localization applied)
        // - b, B: boolean
        // - h, H: hash code (hexadecimal)
        // - s: string (just toString())
        // - c: character
        // - n: platform line separator
        // - %: literal percent
        // Note: Uppercase S and C perform locale-dependent case conversion, so they're excluded
        return conversions.all { it in localeIndependentConversions }
    }

    companion object {
        private val formatCallIds = listOf(
            CallableId(FqName("kotlin.text"), Name.identifier("format")),
        ).associateBy { it.callableName.asString() }

        private val localeClassId = ClassId(FqName("java.util"), Name.identifier("Locale"))

        // Index of the conversion character group in the format specifier regex
        private const val CONVERSION_GROUP_INDEX = 3

        // Locale-independent format conversions
        private val localeIndependentConversions = setOf(
            "x", "X", // hexadecimal integer
            "o", // octal integer
            "a", "A", // hexadecimal floating-point
            "b", "B", // boolean
            "h", "H", // hash code
            "s", // string (lowercase only - uppercase S is locale-dependent)
            "c", // character (lowercase only - uppercase C is locale-dependent)
            "n", // line separator
            "%", // literal percent
        )
    }
}
