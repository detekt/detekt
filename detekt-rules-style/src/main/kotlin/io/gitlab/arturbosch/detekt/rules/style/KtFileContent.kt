package io.gitlab.arturbosch.detekt.rules.style

import org.jetbrains.kotlin.psi.KtFile

internal data class KtFileContent(val file: KtFile, val content: Sequence<String>)

internal fun KtFile.toFileContent() = KtFileContent(this, text.splitToSequence("\n"))
