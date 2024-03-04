package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.psi.KtClass

class TestProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("test")
    override fun instance(): RuleSet = RuleSet(ruleSetId, listOf(::TestRule))
}

class TestRule(config: Config) : Rule(config, "A failure") {
    override fun visitClass(klass: KtClass) {
        if (klass.name == "Poko") {
            report(CodeSmell(Entity.from(klass), description))
        }
    }
}
