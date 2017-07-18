package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtFile

abstract class SubRule<in Element>(config: Config, private val element: PsiElement) : Rule(config) {
	abstract fun apply(element: Element)

	fun isActive(): Boolean {
		val file: KtFile? = PsiTreeUtil.getParentOfType(element, KtFile::class.java, false)
		file?.let {
			return visitCondition(it)
		}
		return false
	}
}