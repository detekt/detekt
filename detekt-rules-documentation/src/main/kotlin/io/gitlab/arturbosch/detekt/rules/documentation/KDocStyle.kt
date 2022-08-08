package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtDeclaration

class KDocStyle(config: Config = Config.empty) :
    @Suppress("DEPRECATION")
    io.gitlab.arturbosch.detekt.api.MultiRule() {

    private val deprecatedBlockTag = DeprecatedBlockTag(config)
    private val endOfSentenceFormat = EndOfSentenceFormat(config)

    override val rules = listOf(
        deprecatedBlockTag,
        endOfSentenceFormat
    )

    override fun visitDeclaration(dcl: KtDeclaration) {
        deprecatedBlockTag.visitDeclaration(dcl)
        endOfSentenceFormat.visitDeclaration(dcl)
    }
}
