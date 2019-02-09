package io.gitlab.arturbosch.detekt.rules

import java.util.ArrayDeque
import kotlin.reflect.KClass
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiCommentImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiCoreCommentImpl
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.api.KDocElement
import org.jetbrains.kotlin.kdoc.psi.impl.KDocElementImpl
import org.jetbrains.kotlin.kdoc.psi.impl.KDocImpl
import org.jetbrains.kotlin.kdoc.psi.impl.KDocLink
import org.jetbrains.kotlin.kdoc.psi.impl.KDocName
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

fun ASTNode.tokenSequence(skipTreesOf: Set<KClass<out PsiElement>>): Sequence<ASTNode> = sequence {
    val queue = ArrayDeque<ASTNode>()
    queue.add(this@tokenSequence)
    do {
        val curr = queue.pop()
        if (curr.psi::class !in skipTreesOf) {
            // Yields only tokens which can be identified in the source code.
            // Composite elements, e.g. classes or files, are abstractions over many leaf nodes.
            if (curr is LeafElement) {
                yield(curr)
            }
            queue.addAll(curr.getChildren(null))
        }
    } while (queue.isNotEmpty())
}

fun KtFile.linesOfCode(): Int = linesOfCode(this)
fun KtElement.linesOfCode(inFile: KtFile = this.containingKtFile): Int = node.tokenSequence(comments)
        .map { it.line(inFile) }
        .distinct()
        .count()

fun ASTNode.line(inFile: KtFile) = DiagnosticUtils.getLineAndColumnInPsiFile(inFile, this.textRange).line

private val comments: Set<KClass<out PsiElement>> = setOf(
        PsiWhiteSpace::class,
        PsiWhiteSpaceImpl::class,
        PsiComment::class,
        PsiCommentImpl::class,
        PsiCoreCommentImpl::class,
        KDoc::class,
        KDocImpl::class,
        KDocElementImpl::class,
        KDocElement::class,
        KDocLink::class,
        KDocSection::class,
        KDocTag::class,
        KDocName::class
)
