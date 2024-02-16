package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
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
class UseArrayLiteralsInAnnotations(config: Config) : Rule(
    config,
    "Array literals [...] should be preferred as they are more readable than `arrayOf(...)` expressions.",
) {

    override fun visitAnnotationEntry(annotationEntry: KtAnnotationEntry) {
        for (argument in annotationEntry.valueArguments) {
            if (argument.getArgumentExpression().isArrayOfFunctionCall()) {
                report(CodeSmell(Entity.from(argument.asElement()), description))
            }
        }
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        if (constructor.containingClass()?.isAnnotation() != true) return
        for (parameter in constructor.valueParameters) {
            val defaultValue = parameter.defaultValue ?: continue
            if (defaultValue.isArrayOfFunctionCall()) {
                report(CodeSmell(Entity.from(defaultValue), description))
            }
        }
    }

    private fun KtExpression?.isArrayOfFunctionCall(): Boolean =
        (this as? KtCallExpression)?.calleeExpression?.text in arrayOfFunctions

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
