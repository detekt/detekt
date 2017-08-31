package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

class DataClassRule(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue("DataClassRule",
            Severity.CodeSmell,
            "Data class should be use to keep only the data.")

    override fun visitClass(klass: KtClass) {
        if (!klass.isData()) return
        super.visitClass(klass)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!(function.modifierList?.hasModifier(KtTokens.OVERRIDE_KEYWORD) ?: false)) {
            report(CodeSmell(issue, Entity.from(function)))
        }
    }
}
