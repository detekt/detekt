package io.gitlab.arturbosch.detekt.cli.out

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.lang.Language
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.PsiReference
import org.jetbrains.kotlin.com.intellij.psi.ResolveState
import org.jetbrains.kotlin.com.intellij.psi.scope.PsiScopeProcessor
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.com.intellij.psi.search.SearchScope
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtVisitor
import javax.swing.Icon

class FakeKtElement(
    text: String
) : KtElement {
    private val psiFile = FakePsiFile(text)

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D): R {
        throw IllegalStateException("not mocked")
    }

    override fun accept(p0: PsiElementVisitor) {
        throw IllegalStateException("not mocked")
    }

    override fun <D> acceptChildren(visitor: KtVisitor<Void, D>, data: D) {
        throw IllegalStateException("not mocked")
    }

    override fun acceptChildren(p0: PsiElementVisitor) {
        throw IllegalStateException("not mocked")
    }

    override fun add(p0: PsiElement): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun addAfter(p0: PsiElement, p1: PsiElement?): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun addBefore(p0: PsiElement, p1: PsiElement?): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun addRange(p0: PsiElement?, p1: PsiElement?): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun addRangeAfter(p0: PsiElement?, p1: PsiElement?, p2: PsiElement?): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun addRangeBefore(p0: PsiElement, p1: PsiElement, p2: PsiElement?): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun canNavigate(): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun canNavigateToSource(): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun checkAdd(p0: PsiElement) {
        throw IllegalStateException("not mocked")
    }

    override fun checkDelete() {
        throw IllegalStateException("not mocked")
    }

    override fun copy(): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun delete() {
        throw IllegalStateException("not mocked")
    }

    override fun deleteChildRange(p0: PsiElement?, p1: PsiElement?) {
        throw IllegalStateException("not mocked")
    }

    override fun findElementAt(p0: Int): PsiElement? {
        throw IllegalStateException("not mocked")
    }

    override fun findReferenceAt(p0: Int): PsiReference? {
        throw IllegalStateException("not mocked")
    }

    override fun getChildren(): Array<PsiElement> {
        throw IllegalStateException("not mocked")
    }

    override fun getContainingFile(): PsiFile = psiFile

    override fun getContainingKtFile(): KtFile {
        throw IllegalStateException("not mocked")
    }

    override fun getContext(): PsiElement? {
        throw IllegalStateException("not mocked")
    }

    override fun <T : Any?> getCopyableUserData(p0: Key<T>?): T? {
        throw IllegalStateException("not mocked")
    }

    override fun getFirstChild(): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun getIcon(p0: Int): Icon {
        throw IllegalStateException("not mocked")
    }

    override fun getLanguage(): Language {
        throw IllegalStateException("not mocked")
    }

    override fun getLastChild(): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun getManager(): PsiManager {
        throw IllegalStateException("not mocked")
    }

    override fun getName(): String? {
        throw IllegalStateException("not mocked")
    }

    override fun getNavigationElement(): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun getNextSibling(): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun getNode(): ASTNode {
        throw IllegalStateException("not mocked")
    }

    override fun getOriginalElement(): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun getParent(): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun getPrevSibling(): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun getProject(): Project {
        throw IllegalStateException("not mocked")
    }

    override fun getPsiOrParent(): KtElement {
        throw IllegalStateException("not mocked")
    }

    override fun getReference(): PsiReference? {
        throw IllegalStateException("not mocked")
    }

    override fun getReferences(): Array<PsiReference> {
        throw IllegalStateException("not mocked")
    }

    override fun getResolveScope(): GlobalSearchScope {
        throw IllegalStateException("not mocked")
    }

    override fun getStartOffsetInParent(): Int {
        throw IllegalStateException("not mocked")
    }

    override fun getText(): String {
        throw IllegalStateException("not mocked")
    }

    override fun getTextLength(): Int {
        throw IllegalStateException("not mocked")
    }

    override fun getTextOffset(): Int {
        throw IllegalStateException("not mocked")
    }

    override fun getTextRange(): TextRange {
        throw IllegalStateException("not mocked")
    }

    override fun getUseScope(): SearchScope {
        throw IllegalStateException("not mocked")
    }

    override fun <T : Any?> getUserData(p0: Key<T>): T? {
        throw IllegalStateException("not mocked")
    }

    override fun isEquivalentTo(p0: PsiElement?): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun isPhysical(): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun isValid(): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun isWritable(): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun navigate(p0: Boolean) {
        throw IllegalStateException("not mocked")
    }

    override fun processDeclarations(
        p0: PsiScopeProcessor,
        p1: ResolveState,
        p2: PsiElement?,
        p3: PsiElement
    ): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun <T : Any?> putCopyableUserData(p0: Key<T>?, p1: T?) {
        throw IllegalStateException("not mocked")
    }

    override fun <T : Any?> putUserData(p0: Key<T>, p1: T?) {
        throw IllegalStateException("not mocked")
    }

    override fun replace(p0: PsiElement): PsiElement {
        throw IllegalStateException("not mocked")
    }

    override fun textContains(p0: Char): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun textMatches(p0: CharSequence): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun textMatches(p0: PsiElement): Boolean {
        throw IllegalStateException("not mocked")
    }

    override fun textToCharArray(): CharArray {
        throw IllegalStateException("not mocked")
    }
}
