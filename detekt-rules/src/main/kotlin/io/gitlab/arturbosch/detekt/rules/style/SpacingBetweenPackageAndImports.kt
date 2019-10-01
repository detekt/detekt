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
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.siblings

/**
 * This rule verifies spacing between package and import statements as well as between import statements and class
 * declarations.
 *
 * <noncompliant>
 * package foo
 * import a.b
 * class Bar { }
 * </noncompliant>
 *
 * <compliant>
 * package foo
 *
 * import a.b
 *
 * class Bar { }
 * </compliant>
 */
class SpacingBetweenPackageAndImports(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "Violation of the package declaration style.",
            Debt.FIVE_MINS)

    override fun visitKtFile(file: KtFile) {
        if (file.hasPackage() && file.anyDescendantOfType<KtClassOrObject>()) {
            file.importList?.let {
                if (it.imports.isNotEmpty()) {
                    checkPackageDeclaration(it)
                    checkKtElementsDeclaration(it)
                }
            }
        }
    }

    private fun KtFile.hasPackage() = packageDirective?.name?.isNotEmpty() == true

    private fun checkPackageDeclaration(importList: KtImportList) {
        val prevSibling = importList.prevSibling
        if (isPackageDeclaration(prevSibling) || prevSibling is PsiWhiteSpace) {
            checkLinebreakAfterElement(prevSibling, "There should be exactly one empty line in between the " +
                    "package declaration and the list of imports.")
        }
    }

    private fun isPackageDeclaration(element: PsiElement?) =
            element is KtPackageDirective && element.text.isNotEmpty()

    private fun checkKtElementsDeclaration(importList: KtImportList) {
        val ktElement = importList.siblings(withItself = false).filterIsInstance<KtElement>().firstOrNull() ?: return
        val nextSibling = importList.nextSibling
        if (nextSibling is PsiWhiteSpace || nextSibling is KtElement) {
            val name = (ktElement as? KtClassOrObject)?.name ?: "the class or object"

            checkLinebreakAfterElement(nextSibling, "There should be exactly one empty line in between the " +
                    "list of imports and the declaration of $name.")
        }
    }

    private fun checkLinebreakAfterElement(element: PsiElement?, message: String) {
        if (element is PsiWhiteSpace || element is KtElement) {
            val count = element.text.count { it == '\n' }
            if (count != 2) {
                report(CodeSmell(issue, Entity.from(element), message))
            }
        }
    }
}
