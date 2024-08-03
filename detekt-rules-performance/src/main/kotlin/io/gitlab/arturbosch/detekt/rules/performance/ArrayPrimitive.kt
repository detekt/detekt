package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Using `Array<Primitive>` leads to implicit boxing and performance hit. Prefer using Kotlin specialized Array
 * Instances.
 *
 * As stated in the Kotlin [documentation](https://kotlinlang.org/docs/basic-types.html#arrays) Kotlin has
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
    Rule(
        config,
        "Using `Array<Primitive>` leads to implicit boxing and a performance hit."
    ),
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.calleeExpression?.text !in factoryMethodNames) return

        val descriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor
        if (descriptor != null && isArrayPrimitive(descriptor)) {
            report(CodeSmell(Entity.from(expression), description))
        }
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
            .forEach { report(CodeSmell(Entity.from(it), description)) }
    }

    private fun isArrayPrimitive(descriptor: CallableDescriptor): Boolean {
        val type = descriptor.returnType?.arguments?.singleOrNull()?.type
        return descriptor.fqNameOrNull() in factoryMethodFqNames && type != null && KotlinBuiltIns.isPrimitiveType(type)
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
        private val factoryMethodFqNames = listOf(FqName("kotlin.arrayOf"), FqName("kotlin.emptyArray"))
        private val factoryMethodNames = factoryMethodFqNames.map { it.shortName().asString() }
    }
}
