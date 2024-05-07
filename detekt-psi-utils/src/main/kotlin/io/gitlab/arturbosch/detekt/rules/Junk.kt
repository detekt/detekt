package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression

fun KtClass.companionObject() = this.companionObjects.singleOrNull { it.isCompanion() }

fun KtCallExpression.receiverIsUsed(context: BindingContext): Boolean =
    (parent as? KtQualifiedExpression)?.let {
        val scopeOfApplyCall = parent.parent
        !(
            (scopeOfApplyCall == null || scopeOfApplyCall is KtBlockExpression) &&
                (context == BindingContext.EMPTY || !it.isUsedAsExpression(context))
            )
    } ?: true
