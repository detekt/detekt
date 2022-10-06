package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.util.containingNonLocalDeclaration

fun KotlinType.fqNameOrNull(): FqName? {
    return TypeUtils.getClassDescriptor(this)?.fqNameOrNull()
}

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
