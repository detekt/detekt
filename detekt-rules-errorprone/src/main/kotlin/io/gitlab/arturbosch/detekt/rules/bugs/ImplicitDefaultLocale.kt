package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

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
    RequiresTypeResolution {
    private val formatCalls = listOf(
        FqName("kotlin.text.format")
    )

    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)
        checkStringFormatting(expression)
    }

    private fun checkStringFormatting(expression: KtQualifiedExpression) {
        if (expression.getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameSafe in formatCalls &&
            expression.containsLocaleObject(bindingContext).not()
        ) {
            report(
                CodeSmell(
                    Entity.from(expression),
                    "${expression.text} uses implicitly default locale for string formatting."
                )
            )
        }
    }
}

private fun KtQualifiedExpression.containsLocaleObject(bindingContext: BindingContext): Boolean {
    val lastCallExpression = lastChild as? KtCallExpression
    val firstArgument = lastCallExpression
        ?.valueArguments
        ?.firstOrNull()
        ?: return false
    return firstArgument.getArgumentExpression()
        .getResolvedCall(bindingContext)
        ?.resultingDescriptor
        ?.fqNameSafe
        ?.startsWith(
            FqName(
                "java.util.Locale"
            )
        ) == true
}
