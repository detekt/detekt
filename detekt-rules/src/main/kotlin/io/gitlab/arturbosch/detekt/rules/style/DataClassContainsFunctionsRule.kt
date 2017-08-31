package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

class DataClassContainsFunctionsRule(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue("DataClassContainsFunctions",
            Severity.Style,
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
