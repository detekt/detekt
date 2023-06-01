package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * This rule detects annotations which use the arrayOf(...) syntax instead of the array literal [...] syntax.
 * The latter should be preferred as it is more readable.
 *
 * <noncompliant>
 * @@PositiveCase(arrayOf("..."))
 * </noncompliant>
 *
 * <compliant>
 * @@NegativeCase(["..."])
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class UseArrayLiteralsInAnnotations(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Array literals [...] should be preferred as they are more readable than `arrayOf(...)` expressions.",
        Debt.FIVE_MINS
    )

    override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
        for (argument in annotationEntry.valueArguments) {
            if (argument.getArgumentExpression().isArrayOfFunctionCall()) {
                report(CodeSmell(issue, Entity.from(argument.asElement()), issue.description))
            }
        }
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        if (constructor.containingClass()?.isAnnotation() != true) return
        for (parameter in constructor.valueParameters) {
            val defaultValue = parameter.defaultValue ?: continue
            if (defaultValue.isArrayOfFunctionCall()) {
                report(CodeSmell(issue, Entity.from(defaultValue), issue.description))
            }
        }
    }

    private fun KtExpression?.isArrayOfFunctionCall(): Boolean =
        this?.safeAs<KtCallExpression>()?.calleeExpression?.text in arrayOfFunctions

    companion object {
        private val arrayOfFunctions = listOf(
            "boolean",
            "char",
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "ubyte",
            "ushort",
            "uint",
            "ulong",
        ).map { "${it}ArrayOf" }.toSet() + "arrayOf"
    }
}
