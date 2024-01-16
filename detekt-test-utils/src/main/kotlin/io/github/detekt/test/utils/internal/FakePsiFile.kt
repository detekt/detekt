package io.github.detekt.test.utils.internal

import org.jetbrains.kotlin.com.intellij.lang.FileASTNode
import org.jetbrains.kotlin.com.intellij.lang.Language
import org.jetbrains.kotlin.com.intellij.navigation.ItemPresentation
import org.jetbrains.kotlin.com.intellij.openapi.fileTypes.FileType
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.psi.FileViewProvider
import org.jetbrains.kotlin.com.intellij.psi.PsiDirectory
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.com.intellij.psi.PsiFileSystemItem
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.PsiReference
import org.jetbrains.kotlin.com.intellij.psi.ResolveState
import org.jetbrains.kotlin.com.intellij.psi.scope.PsiScopeProcessor
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.com.intellij.psi.search.PsiElementProcessor
import org.jetbrains.kotlin.com.intellij.psi.search.SearchScope
import javax.swing.Icon

class FakePsiFile(private val text: String = "", private val name: String = "") : PsiFile {

    override fun navigate(p0: Boolean) {
        // no-op
    }

    override fun canNavigate(): Boolean = false

    override fun canNavigateToSource(): Boolean = false

    override fun getName(): String = name

    override fun getPresentation(): ItemPresentation? = null

    override fun getIcon(p0: Int): Icon {
        error("Fake not implemented yet")
    }

    override fun <T : Any?> getUserData(p0: Key<T>): T? = null

    override fun <T : Any?> putUserData(p0: Key<T>, p1: T?) {
        // no-op
    }

    override fun getProject(): Project {
        error("Fake not implemented yet")
    }

    override fun getLanguage(): Language {
        error("Fake not implemented yet")
    }

    override fun getManager(): PsiManager {
        error("Fake not implemented yet")
    }

    override fun getChildren(): Array<PsiElement> {
        error("Fake not implemented yet")
    }

    override fun getParent(): PsiDirectory? = null

    override fun getFirstChild(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getLastChild(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getNextSibling(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getPrevSibling(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getContainingFile(): PsiFile {
        error("Fake not implemented yet")
    }

    override fun getTextRange(): TextRange {
        error("Fake not implemented yet")
    }

    override fun getStartOffsetInParent(): Int = 0

    override fun getTextLength(): Int = 0

    override fun findElementAt(p0: Int): PsiElement? = null

    override fun findReferenceAt(p0: Int): PsiReference? = null

    override fun getTextOffset(): Int = 0

    override fun getText(): String = text

    override fun textToCharArray(): CharArray = "".toCharArray()

    override fun getNavigationElement(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun getOriginalElement(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun textMatches(p0: CharSequence): Boolean = false

    override fun textMatches(p0: PsiElement): Boolean = false

    override fun textContains(p0: Char): Boolean = false

    override fun accept(p0: PsiElementVisitor) {
        // no-op
    }

    override fun acceptChildren(p0: PsiElementVisitor) {
        // no-op
    }

    override fun copy(): PsiElement {
        error("Fake not implemented yet")
    }

    override fun add(p0: PsiElement): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addBefore(p0: PsiElement, p1: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addAfter(p0: PsiElement, p1: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    @Deprecated("Deprecated in Java")
    override fun checkAdd(p0: PsiElement) {
        error("Fake not implemented yet")
    }

    override fun addRange(p0: PsiElement?, p1: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addRangeBefore(p0: PsiElement, p1: PsiElement, p2: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun addRangeAfter(p0: PsiElement?, p1: PsiElement?, p2: PsiElement?): PsiElement {
        error("Fake not implemented yet")
    }

    override fun delete() {
        // no-op
    }

    @Deprecated("Deprecated in Java")
    override fun checkDelete() {
        // no-op
    }

    override fun deleteChildRange(p0: PsiElement?, p1: PsiElement?) {
        // no-op
    }

    override fun replace(p0: PsiElement): PsiElement {
        error("Fake not implemented yet")
    }

    override fun isValid(): Boolean = false

    override fun isWritable(): Boolean = false

    override fun getReference(): PsiReference? = null

    override fun getReferences(): Array<PsiReference> {
        error("Fake not implemented yet")
    }

    override fun <T : Any?> getCopyableUserData(p0: Key<T>): T? = null

    override fun <T : Any?> putCopyableUserData(p0: Key<T>, p1: T?) {
        // no-op
    }

    override fun processDeclarations(
        p0: PsiScopeProcessor,
        p1: ResolveState,
        p2: PsiElement?,
        p3: PsiElement
    ): Boolean = false

    override fun getContext(): PsiElement? = null

    override fun isPhysical(): Boolean = false

    override fun getResolveScope(): GlobalSearchScope {
        error("Fake not implemented yet")
    }

    override fun getUseScope(): SearchScope {
        error("Fake not implemented yet")
    }

    override fun getNode(): FileASTNode {
        error("Fake not implemented yet")
    }

    override fun isEquivalentTo(p0: PsiElement?): Boolean = false

    override fun setName(p0: String): PsiElement {
        error("Fake not implemented yet")
    }

    override fun checkSetName(p0: String?) {
        // no-op
    }

    override fun isDirectory(): Boolean = false

    override fun getVirtualFile(): VirtualFile {
        error("Fake not implemented yet")
    }

    override fun processChildren(p0: PsiElementProcessor<in PsiFileSystemItem>): Boolean = false

    override fun getContainingDirectory(): PsiDirectory {
        error("Fake not implemented yet")
    }

    override fun getModificationStamp(): Long = 0L

    override fun getOriginalFile(): PsiFile {
        error("Fake not implemented yet")
    }

    override fun getFileType(): FileType {
        error("Fake not implemented yet")
    }

    @Deprecated("Deprecated in Java")
    override fun getPsiRoots(): Array<PsiFile> {
        error("Fake not implemented yet")
    }

    override fun getViewProvider(): FileViewProvider {
        error("Fake not implemented yet")
    }

    override fun subtreeChanged() {
        // no-op
    }
}
