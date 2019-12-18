package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Using Array<Primitive> leads to implicit boxing and performance hit. Prefer using Kotlin specialized Array
 * Instances.
 *
 * As stated in the Kotlin [documention](https://kotlinlang.org/docs/reference/basic-types.html#arrays) Kotlin has
 * specialized arrays to represent primitive types without boxing overhead, such as `IntArray`, `ByteArray` and so on.
 *
 * <noncompliant>
 * fun function(array: Array<Int>) { }
 *
 * fun returningFunction(): Array<Double> { }
 * </noncompliant>
 *
 * <compliant>
 * fun function(array: IntArray) { }
 *
 * fun returningFunction(): DoubleArray { }
 * </compliant>
 *
 * @active since v1.2.0
 */
class ArrayPrimitive(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ArrayPrimitive",
            Severity.Performance,
            "Using Array<Primitive> leads to implicit boxing and a performance hit",
            Debt.FIVE_MINS)

    private val primitiveTypes = hashSetOf(
            "Int",
            "Double",
            "Float",
            "Short",
            "Byte",
            "Long",
            "Char"
    )

    override fun visitParameter(parameter: KtParameter) {
        val typeReference = parameter.typeReference
        if (typeReference != null) {
            reportArrayPrimitives(typeReference)
        }
        super.visitParameter(parameter)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.hasDeclaredReturnType()) {
            val typeReference = function.typeReference
            if (typeReference != null) {
                reportArrayPrimitives(typeReference)
            }
        }
        super.visitNamedFunction(function)
    }

    private fun reportArrayPrimitives(element: KtElement) {
        return element
                .collectDescendantsOfType<KtTypeReference> { isArrayPrimitive(it) }
                .forEach { report(CodeSmell(issue, Entity.from(it), issue.description)) }
    }

    private fun isArrayPrimitive(it: KtTypeReference): Boolean {
        if (it.text?.startsWith("Array<") == true) {
            val genericTypeArguments = it.typeElement?.typeArgumentsAsTypes
            return genericTypeArguments?.singleOrNull()?.let { primitiveTypes.contains(it.text) } == true
        }
        return false
    }
}
