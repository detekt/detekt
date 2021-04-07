package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import org.jetbrains.kotlin.psi.KtDeclaration

class KDocStyle(config: Config = Config.empty) : MultiRule() {

    private val endOfSentenceFormat = EndOfSentenceFormat(config)

    override val rules = listOf(
        endOfSentenceFormat
    )

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        endOfSentenceFormat.verify(dcl)
    }
}
