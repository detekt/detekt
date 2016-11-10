package io.gitlab.arturbosch.detekt.rules

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import kotlin.reflect.KClass

/**
 * @author Artur Bosch
 */

fun KtExpression?.asBlockExpression(): KtBlockExpression? = if (this is KtBlockExpression) this else null

fun KtModifierListOwner.isPublicNotOverriden() = this.hasModifier(KtTokens.PUBLIC_KEYWORD)
		|| (this.hasModifier(KtTokens.PRIVATE_KEYWORD) || this.hasModifier(KtTokens.PROTECTED_KEYWORD)
		|| this.hasModifier(KtTokens.INTERNAL_KEYWORD) || this.hasModifier(KtTokens.OVERRIDE_KEYWORD)).not()

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
	"run", "let", "apply", "with", "use", "forEach" -> true
	else -> false
}

internal fun PsiElement.isPartOf(clazz: KClass<out PsiElement>) = getNonStrictParentOfType(clazz.java) != null
internal fun PsiElement.isPartOfString() = isPartOf(org.jetbrains.kotlin.psi.KtStringTemplateEntry::class)

internal fun <T> List<T>.head() = this.subList(0, this.size - 1)
internal fun <T> List<T>.tail() = this.subList(1, this.size)
