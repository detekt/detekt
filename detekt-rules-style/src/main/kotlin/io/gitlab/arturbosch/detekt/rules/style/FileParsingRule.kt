package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtFile

class FileParsingRule(val config: Config = Config.empty) :
    @Suppress("DEPRECATION")
    io.gitlab.arturbosch.detekt.api.MultiRule() {

    private val maxLineLength = MaxLineLength(config)
    private val trailingWhitespace = TrailingWhitespace(config)
    private val noTabs = NoTabs(config)
    override val rules = listOf(maxLineLength, trailingWhitespace, noTabs)

    override fun visitKtFile(file: KtFile) {
        val lines = file.text.splitToSequence("\n")
        val fileContents = KtFileContent(file, lines)

        maxLineLength.runIfActive { visit(fileContents) }
        trailingWhitespace.runIfActive { visit(fileContents) }
        noTabs.runIfActive { findTabs(file) }
    }
}

data class KtFileContent(val file: KtFile, val content: Sequence<String>)
