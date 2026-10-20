package dev.detekt.rules.ktlintwrapper

import com.intellij.openapi.util.Key
import dev.detekt.api.modifiedText
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.UserDataProperty

/**
 * A single physical copy of a Kotlin file, shared by every ktlint wrapping rule that visits it.
 *
 * detekt runs each ktlint wrapper as its own rule, so a file is visited once per active wrapping.
 * The analysed [KtFile] is read-only and ktlint autocorrect mutates the tree, so a mutable copy is
 * required; parsing a fresh copy on every visit re-parses each file once per wrapping. Instead the
 * first wrapping to visit a file parses it once and caches the copy here, and later wrappings reuse
 * it -- walking and (for autocorrect) mutating the same tree in sequence, as the pre-2.0 formatting
 * rule did over a single shared tree.
 */
internal var KtFile.ktlintFileCopy: KtFile? by UserDataProperty(Key("ktlintFileCopy"))

/**
 * Returns the shared mutable copy of [root], creating it if absent or if the cached copy no longer
 * matches [root]'s current content. The cache is validated against `modifiedText ?: text`: once an
 * autocorrect (or a test) changes that content, the cached copy is replaced, so a reused [KtFile] is
 * never served a stale copy.
 */
internal fun sharedFileCopy(root: KtFile): KtFile {
    val expected = root.modifiedText ?: root.text
    val cached = root.ktlintFileCopy
    if (cached != null && cached.text == expected) {
        return cached
    }
    return KtPsiFactory(root.project)
        .createPhysicalFile(root.name, expected)
        .also { root.ktlintFileCopy = it }
}
