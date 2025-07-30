package dev.detekt.psi.testfixtures

import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtVisitor
import javax.swing.Icon

class FakeKtElement(private val psiFile: PsiFile = FakePsiFile("")) : KtElement {

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R {
        error("Fake not implemented yet")
    }

    override fun accept(p0: PsiElementVisitor) {
        // no-op
    }

    override fun <D> acceptChildren(visitor: KtVisitor<Void, D>, data: D) {
        // no-op
    }

    override fun acceptChildren(p0: PsiElementVisitor) {
        // no-op
    }

    override fun add(p0: PsiElement): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addAfter(p0: PsiElement, p1: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addBefore(p0: PsiElement, p1: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addRange(p0: PsiElement?, p1: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addRangeAfter(p0: PsiElement?, p1: PsiElement?, p2: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addRangeBefore(p0: PsiElement, p1: PsiElement, p2: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun canNavigate(): Boolean = false

    override fun canNavigateToSource(): Boolean = false

    @Deprecated("Deprecated in PsiElement interface")
    override fun checkAdd(p0: PsiElement) {
        // no-op
    }

    @Deprecated("Deprecated in PsiElement interface")
    override fun checkDelete() {
        // no-op
    }

    override fun copy(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun delete() {
        // no-op
    }

    override fun deleteChildRange(p0: PsiElement?, p1: PsiElement?) {
        // no-op
    }

    override fun findElementAt(p0: Int): PsiElement? {
        error("Fake not implemented yet")
    }

    override fun findReferenceAt(p0: Int): PsiReference? {
        error("Fake not implemented yet")
    }

    override fun getChildren(): Array<PsiElement> {
        error("Fake not implemented yet")
    }

    override fun getContainingFile(): PsiFile = psiFile

    override fun getContainingKtFile(): KtFile {
        error("Fake not implemented yet")
    }

    override fun getContext(): PsiElement? {
        error("Fake not implemented yet")
    }

    override fun <T : Any?> getCopyableUserData(p0: Key<T>): T? {
        error("Fake not implemented yet")
    }

    override fun getFirstChild(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getIcon(p0: Int): Icon {
        error("Fake not implemented yet")
    }

    override fun getLanguage(): Language {
        error("Fake not implemented yet")
    }

    override fun getLastChild(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getManager(): PsiManager {
        error("Fake not implemented yet")
    }

    override fun getName(): String? = null

    override fun getNavigationElement(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getNextSibling(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getNode(): ASTNode {
        error("Fake not implemented yet")
    }

    override fun getOriginalElement(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getParent(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getPresentation(): ItemPresentation? {
        error("Fake not implemented yet")
    }

    override fun getPrevSibling(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getProject(): Project {
        error("Fake not implemented yet")
    }

    override fun getPsiOrParent(): KtElement {
        error("Fake not implemented yet")
    }

    @Deprecated("Don't use getReference() on KtElement for the choice is unpredictable")
    override fun getReference(): PsiReference? {
        error("Fake not implemented yet")
    }

    override fun getReferences(): Array<PsiReference> {
        error("Fake not implemented yet")
    }

    override fun getResolveScope(): GlobalSearchScope {
        error("Fake not implemented yet")
    }

    override fun getStartOffsetInParent(): Int = 0

    override fun getText(): String = ""

    override fun getTextLength(): Int = 0

    override fun getTextOffset(): Int = 0

    override fun getTextRange(): TextRange {
        error("Fake not implemented yet")
    }

    override fun getUseScope(): SearchScope {
        error("Fake not implemented yet")
    }

    override fun <T : Any?> getUserData(p0: Key<T>): T? {
        error("Fake not implemented yet")
    }

    override fun isEquivalentTo(p0: PsiElement?): Boolean = false

    override fun isPhysical(): Boolean = false

    override fun isValid(): Boolean = false

    override fun isWritable(): Boolean = false

    override fun navigate(p0: Boolean) {
        // no-op
    }

    override fun processDeclarations(
        p0: PsiScopeProcessor,
        p1: ResolveState,
        p2: PsiElement?,
        p3: PsiElement,
    ): Boolean = false

    override fun <T : Any?> putCopyableUserData(p0: Key<T>, p1: T?) {
        // no-op
    }

    override fun <T : Any?> putUserData(p0: Key<T>, p1: T?) {
        // no-op
    }

    override fun replace(p0: PsiElement): PsiElement {
        error("Fake not implemented yet")
    }

    override fun textContains(p0: Char): Boolean = false

    override fun textMatches(p0: CharSequence): Boolean = false

    override fun textMatches(p0: PsiElement): Boolean = false

    override fun textToCharArray(): CharArray = "".toCharArray()

    override fun toString() = "FakeKtElement"
}
