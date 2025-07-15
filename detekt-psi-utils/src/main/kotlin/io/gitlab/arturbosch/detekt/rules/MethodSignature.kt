package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isUnit

fun KtFunction.isEqualsFunction() =
    this.name == "equals" &&
        this.isOverride() &&
        hasCorrectEqualsParameter() &&
        this.receiverTypeReference == null

fun KtFunction.isHashCodeFunction() =
    this.name == "hashCode" &&
        this.isOverride() &&
        this.valueParameters.isEmpty() &&
        this.receiverTypeReference == null

/**
 * [Kotlin Documentation](https://kotlinlang.org/docs/java-interop.html#finalize)
 */
fun KtNamedFunction.isJvmFinalizeFunction() =
    this.name == "finalize" &&
        this.valueParameters.isEmpty() &&
        !this.isOverride() &&
        !this.isPrivate()

private val knownAnys = setOf("Any?", "kotlin.Any?")
fun KtFunction.hasCorrectEqualsParameter() =
    this.valueParameters.singleOrNull()?.typeReference?.text in knownAnys

fun KtNamedFunction.isMainFunction() = hasMainSignature() && (this.isTopLevel || isMainInsideObject())

private fun KtNamedFunction.hasMainSignature() =
    this.name == "main" && this.isPublicNotOverridden() && this.hasMainParameter()

private fun KtNamedFunction.hasMainParameter() =
    valueParameters.isEmpty() ||
        (valueParameters.size == 1 && valueParameters[0].typeReference?.text == "Array<String>") ||
        (valueParameters.size == 1 && valueParameters[0].isVarArg && valueParameters[0].typeReference?.text == "String")

private fun KtNamedFunction.isMainInsideObject() =
    this.name == "main" &&
        this.isPublicNotOverridden() &&
        this.parent?.parent is KtObjectDeclaration &&
        this.hasAnnotation("JvmStatic", "kotlin.jvm.JvmStatic")

fun KtNamedFunction.hasImplicitUnitReturnType(bindingContext: BindingContext) =
    bodyExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType?.isUnit() == true

fun KtNamedFunction.hasImplicitUnitReturnType(): Boolean {
    if (hasBlockBody()) return false

    return analyze(this) {
        bodyExpression?.expressionType?.isUnitType == true
    }
}
