package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.isFlexible
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.util.containingNonLocalDeclaration

fun KotlinType.fqNameOrNull(): FqName? {
    return TypeUtils.getClassDescriptor(this)?.fqNameOrNull()
}

fun KotlinType?.isString(): Boolean = KotlinBuiltIns.isString(this)

fun KotlinType.isPrimitiveType(): Boolean = KotlinBuiltIns.isPrimitiveType(this)

/**
 * Returns types considering data flow.
 *
 * For Example, for `s` in `print(s)` below, [BindingContext.getType] returns String?, but this function returns String.
 *
 * ```kotlin
 * fun foo(s: String?) {
 *     if (s != null) {
 *         println(s) // s is String (smart cast from String?)
 *     }
 * }
 * ```
 */
@Suppress("ReturnCount")
fun KtExpression.getDataFlowAwareTypes(
    bindingContext: BindingContext,
    languageVersionSettings: LanguageVersionSettings,
    dataFlowValueFactory: DataFlowValueFactory,
    originalType: KotlinType? = bindingContext.getType(this),
): Set<KotlinType> {
    require(bindingContext != BindingContext.EMPTY) { "The bindingContext must not be empty" }

    if (originalType == null) return emptySet()

    val dataFlowInfo = bindingContext[BindingContext.EXPRESSION_TYPE_INFO, this]
        ?.dataFlowInfo
        ?: return setOf(originalType)

    val containingDeclaration = containingNonLocalDeclaration()
        ?.let { bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, it] }
        ?: return setOf(originalType)

    val dataFlowValue = dataFlowValueFactory
        .createDataFlowValue(this, originalType, bindingContext, containingDeclaration)

    return dataFlowInfo.getStableTypes(dataFlowValue, languageVersionSettings)
        .ifEmpty { setOf(originalType) }
}

/**
 * Return if overall expression is nullable or not nullable
 *
 * ```kotlin
 * var a: Int? = null
 * val b = a // RHS expression will be nullable
 * val c = a!! // RHS expression will be not nullable
 * val d = (a ?: error("null assertion message")) // RHS expression will be not nullable
 * val c = 1?.and(2) // RHS expression will be not nullable
 * ```
 * [shouldConsiderPlatformTypeAsNullable] determines the behaviour of platform type. Passing true considers
 * the platform type as nullable otherwise it is not considered nullable in case of false
 */
@Suppress("ReturnCount")
fun KtExpression.isNullable(
    bindingContext: BindingContext,
    languageVersionSettings: LanguageVersionSettings,
    dataFlowValueFactory: DataFlowValueFactory,
    shouldConsiderPlatformTypeAsNullable: Boolean,
): Boolean {
    val safeAccessOperation = (this as? KtSafeQualifiedExpression)?.operationTokenNode as? PsiElement
    if (safeAccessOperation != null) {
        return bindingContext.diagnostics.forElement(safeAccessOperation).none {
            it.factory == Errors.UNNECESSARY_SAFE_CALL
        }
    }
    val originalType = descriptor(bindingContext)?.returnType?.takeIf {
        it.isNullable() && (shouldConsiderPlatformTypeAsNullable || !it.isFlexible())
    } ?: return false
    val dataFlowTypes = getDataFlowAwareTypes(
        bindingContext,
        languageVersionSettings,
        dataFlowValueFactory,
        originalType
    )
    return dataFlowTypes.all { it.isNullable() }
}

private fun KtExpression.descriptor(bindingContext: BindingContext): CallableDescriptor? =
    getResolvedCall(bindingContext)?.resultingDescriptor
