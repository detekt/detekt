package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.psiUtil.siblings

class PackageDeclarationStyle(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Violation of the package declaration style." +
					"There should be exactly one blank line after the package and import declaration",
			Debt.FIVE_MINS)

	override fun visitImportList(importList: KtImportList) {
		if (importList.imports.isNotEmpty()) {
			checkPackageDeclaration(importList)
			checkKtElementsDeclaration(importList)
		}
	}

	private fun checkPackageDeclaration(importList: KtImportList) {
		val prevSibling = importList.prevSibling
		if (isPackageDeclaration(prevSibling) || prevSibling is PsiWhiteSpace) {
			checkLinebreakAfterElement(prevSibling)
		}
	}

	private fun isPackageDeclaration(element: PsiElement?) =
			(element is KtPackageDirective && element.text.isNotEmpty())

	private fun checkKtElementsDeclaration(importList: KtImportList) {
		val hasKtElements = importList.siblings(withItself = false).any { it is KtElement }
		val nextSibling = importList.nextSibling
		if (hasKtElements
				&& (nextSibling is PsiWhiteSpace || nextSibling is KtElement)) {
			checkLinebreakAfterElement(nextSibling)
		}
	}

	private fun checkLinebreakAfterElement(element: PsiElement?) {
		if (element is PsiWhiteSpace || element is KtElement) {
			val count = element.text.count { it == '\n' }
			if (count != 2) {
				report(CodeSmell(issue, Entity.from(element), message = ""))
			}
		}
	}
}
