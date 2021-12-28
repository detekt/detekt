@file:Suppress("DEPRECATION") // FIXME remove PsiCoreCommentImpl in IntelliJ 2020.3

package io.github.detekt.metrics

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

fun ASTNode.tokenSequence(skipTreesOf: Set<Class<out PsiElement>>): Sequence<ASTNode> = sequence {
    val queue = ArrayDeque<ASTNode>()
    queue.add(this@tokenSequence)
    do {
        val curr = queue.removeFirst()
        if (curr.psi::class.java !in skipTreesOf) {
            // Yields only tokens which can be identified in the source code.
            // Composite elements, e.g. classes or files, are abstractions over many leaf nodes.
            if (curr is LeafElement) {
                yield(curr)
            }
            queue.addAll(curr.getChildren(null))
        }
    } while (queue.isNotEmpty())
}

fun KtElement.linesOfCode(inFile: KtFile = this.containingKtFile): Int =
    node.tokenSequence(comments)
        .map { it.line(inFile) }
        .distinct()
        .count()

fun ASTNode.line(inFile: KtFile): Int = try {
    DiagnosticUtils.getLineAndColumnInPsiFile(inFile, this.textRange).line
} catch (@Suppress("SwallowedException", "TooGenericExceptionCaught") e: IndexOutOfBoundsException) {
    // When auto-correctable rules performs actual mutation, KtFile.text is updated but
    // KtFile.viewProvider.document is not updated. This will cause crash in subsequent rules
    // if they are using any function relying on the KtFile.viewProvider.document.
    // The exception is silenced to return -1 while we should seek long-term solution for execution
    // order of rules (#3445)
    -1
}

private val comments: Set<Class<out PsiElement>> = setOf(
    PsiWhiteSpace::class.java,
    PsiWhiteSpaceImpl::class.java,
    PsiComment::class.java,
    PsiCommentImpl::class.java,
    PsiCoreCommentImpl::class.java,
    KDoc::class.java,
    KDocImpl::class.java,
    KDocElementImpl::class.java,
    KDocElement::class.java,
    KDocLink::class.java,
    KDocSection::class.java,
    KDocTag::class.java,
    KDocName::class.java
)
