package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
    "run", "let", "apply", "with", "use", "forEach" -> true
    else -> false
}

fun KtClassOrObject.hasCommentInside() = this.body?.hasCommentInside() ?: false

fun PsiElement.hasCommentInside(): Boolean {
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

fun KtClass.companionObject() = this.companionObjects.singleOrNull { it.isCompanion() }

inline fun <reified T : Any> Any.safeAs(): T? = this as? T

internal fun Config.valueOrDefaultCommaSeparated(
    key: String,
    default: List<String>
): List<String> {
    fun fallBack() = valueOrDefault(key, default.joinToString(","))
        .commaSeparatedPattern()
        .toList()

    return try {
        valueOrDefault(key, default)
    } catch (_: IllegalStateException) {
        fallBack()
    } catch (_: ClassCastException) {
        fallBack()
    }
}
