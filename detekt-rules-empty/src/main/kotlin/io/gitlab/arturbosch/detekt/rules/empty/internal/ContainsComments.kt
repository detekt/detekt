package io.gitlab.arturbosch.detekt.rules.empty.internal

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

fun KtClassOrObject.hasCommentInside() = this.body?.hasCommentInside() ?: false

fun PsiElement.hasCommentInside(): Boolean {
    val commentKey = Key<Boolean>("comment")
    this.acceptChildren(object : KtTreeVisitorVoid() {
        override fun visitComment(comment: PsiComment) {
            putUserData(commentKey, true)
        }
    })
    return getUserData(commentKey) == true
}
