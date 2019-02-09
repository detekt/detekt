package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * @author Artur Bosch
 */

fun KtExpression?.asBlockExpression(): KtBlockExpression? = this as? KtBlockExpression

fun KtClassOrObject.isObjectOfAnonymousClass() =
        this.getNonStrictParentOfType<KtObjectDeclaration>() != null && this.name == null

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
    "run", "let", "apply", "with", "use", "forEach" -> true
    else -> false
}

fun KtBlockExpression.hasCommentInside(): Boolean {
    val commentKey = Key<Boolean>("comment")
    this.acceptChildren(object : DetektVisitor() {
        override fun visitComment(comment: PsiComment?) {
            if (comment != null) putUserData(commentKey, true)
        }
    })
    return getUserData(commentKey) == true
}

fun getIntValueForPsiElement(element: PsiElement): Int? {
    return (element as? KtConstantExpression)?.text?.toIntOrNull()
}

fun KtStringTemplateExpression.plainText() = text.substring(1, text.length - 1)

fun KtClass.companionObject() = this.companionObjects.singleOrNull { it.isCompanion() }

inline fun <reified T : Any> Any.safeAs(): T? = this as? T
