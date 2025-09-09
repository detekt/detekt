package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtQualifiedExpression

fun KtClass.companionObject() = this.companionObjects.singleOrNull { it.isCompanion() }

fun KtCallExpression.receiverIsUsed(): Boolean =
    (parent as? KtQualifiedExpression)?.let {
        val scopeOfApplyCall = parent.parent
        !(
            (scopeOfApplyCall == null || scopeOfApplyCall is KtBlockExpression) &&
                !analyze(it) { it.isUsedAsExpression }
            )
    } ?: true
