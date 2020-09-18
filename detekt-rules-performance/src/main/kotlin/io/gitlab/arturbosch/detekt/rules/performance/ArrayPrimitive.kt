package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.builtins.PrimitiveType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Using Array<Primitive> leads to implicit boxing and performance hit. Prefer using Kotlin specialized Array
 * Instances.
 *
 * As stated in the Kotlin [documentation](https://kotlinlang.org/docs/reference/basic-types.html#arrays) Kotlin has
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
 * @requiresTypeResolution
 */
class ArrayPrimitive(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        "ArrayPrimitive",
        Severity.Performance,
        "Using Array<Primitive> leads to implicit boxing and a performance hit",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        if (expression.calleeExpression?.text !in factoryMethodNames) return
        val descriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return
        if (descriptor.fqNameOrNull() !in factoryMethodFqNames) return

        val type = descriptor.returnType?.arguments?.singleOrNull()?.type ?: return
        if (KotlinBuiltIns.isPrimitiveType(type)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
        super.visitNamedDeclaration(declaration)
        if (declaration is KtCallableDeclaration) {
            reportArrayPrimitives(declaration.typeReference)
            reportArrayPrimitives(declaration.receiverTypeReference)
        }
    }

    private fun reportArrayPrimitives(typeReference: KtTypeReference?) {
        typeReference
            ?.collectDescendantsOfType<KtTypeReference> { isArrayPrimitive(it) }
            ?.forEach { report(CodeSmell(issue, Entity.from(it), issue.description)) }
    }

    private fun isArrayPrimitive(it: KtTypeReference): Boolean {
        if (it.text?.startsWith("Array<") == true) {
            val genericTypeArguments = it.typeElement?.typeArgumentsAsTypes
            return genericTypeArguments?.singleOrNull()?.let { primitiveTypes.contains(it.text) } == true
        }
        return false
    }

    companion object {
        private val primitiveTypes = PrimitiveType.values().map { it.typeName.asString() }
        private val factoryMethodFqNames = listOf(FqName("kotlin.arrayOf"), FqName("kotlin.emptyArray"))
        private val factoryMethodNames = factoryMethodFqNames.map { it.shortName().asString() }
    }
}
