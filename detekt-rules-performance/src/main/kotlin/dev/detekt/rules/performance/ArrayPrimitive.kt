package dev.detekt.rules.performance

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds.BASE_KOTLIN_PACKAGE
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Using `Array<Primitive>` leads to implicit boxing and performance hit. Prefer using Kotlin specialized Array
 * Instances.
 *
 * As stated in the Kotlin [documentation](https://kotlinlang.org/docs/arrays.html#primitive-type-arrays) Kotlin has
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
 */
@ActiveByDefault(since = "1.2.0")
class ArrayPrimitive(config: Config) :
    Rule(config, "Using `Array<Primitive>` leads to implicit boxing and a performance hit."),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.calleeExpression?.text !in factoryMethodNames) return

        if (expression.returnsArrayPrimitive()) {
            report(Finding(Entity.from(expression), description))
        }
    }

    private fun KtCallExpression.returnsArrayPrimitive(): Boolean = analyze(this) {
        val functionCall = resolveToCall()?.singleFunctionCallOrNull() ?: return false
        val returnType = functionCall.partiallyAppliedSymbol.signature.returnType
        return returnType.arrayElementType?.isPrimitive == true
    }

    override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
        super.visitNamedDeclaration(declaration)
        if (declaration is KtCallableDeclaration) {
            declaration.typeReference?.let(this::reportArrayPrimitives)
            declaration.receiverTypeReference?.let(this::reportArrayPrimitives)
        }
    }

    private fun reportArrayPrimitives(typeReference: KtTypeReference) {
        typeReference.collectDescendantsOfType<KtTypeReference> { isArrayPrimitive(it) }
            .forEach { report(Finding(Entity.from(it), description)) }
    }

    private fun isArrayPrimitive(it: KtTypeReference): Boolean {
        if (it.text?.startsWith("Array<") == true) {
            val genericTypeArguments = it.typeElement?.typeArgumentsAsTypes
            return genericTypeArguments?.singleOrNull()?.let { primitiveTypes.contains(it.text) } == true
        }
        return false
    }

    companion object {
        private val primitiveTypes = PrimitiveType.entries.map { it.typeName.asString() }
        private val factoryMethodFqNames = listOf(
            CallableId(BASE_KOTLIN_PACKAGE, Name.identifier("arrayOf")),
            CallableId(BASE_KOTLIN_PACKAGE, Name.identifier("emptyArray"))
        )
        private val factoryMethodNames = factoryMethodFqNames.map { it.callableName.asString() }
    }
}
