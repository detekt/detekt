package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
    "run", "let", "apply", "with", "use", "forEach" -> true
    else -> false
}

fun KtClassOrObject.hasCommentInside() = this.body?.hasCommentInside() ?: false

fun PsiElement.hasCommentInside(): Boolean {
    val commentKey = Key<Boolean>("comment")
    this.acceptChildren(object : KtTreeVisitorVoid() {
        override fun visitComment(comment: PsiComment?) {
            if (comment != null) putUserData(commentKey, true)
        }
    })
    return getUserData(commentKey) == true
}

fun getIntValueForPsiElement(element: PsiElement): Int? {
    return (element as? KtConstantExpression)?.text?.toIntOrNull()
}

fun KtClass.companionObject() = this.companionObjects.singleOrNull { it.isCompanion() }

inline fun <reified T : Any> Any.safeAs(): T? = this as? T

fun KtCallExpression.receiverIsUsed(context: BindingContext): Boolean =
    (parent as? KtQualifiedExpression)?.let {
        val scopeOfApplyCall = parent.parent
        !((scopeOfApplyCall == null || scopeOfApplyCall is KtBlockExpression) &&
            (context == BindingContext.EMPTY || !it.isUsedAsExpression(context)))
    } ?: true
