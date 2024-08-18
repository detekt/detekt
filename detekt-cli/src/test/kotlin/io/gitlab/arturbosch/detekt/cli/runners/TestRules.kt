package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtClass

class TestProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("test")
    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::TestRule,
            ::TestRuleWithDeprecation,
        )
    )
}

internal class TestRule(config: Config) : Rule(config, "A failure") {
    override fun visitClass(klass: KtClass) {
        if (klass.name == "Poko") {
            report(CodeSmell(Entity.from(klass), description))
        }
    }
}

internal class TestRuleWithDeprecation(config: Config) : Rule(config, "A failure") {
    @Suppress("unused")
    @Deprecated("This is deprecated")
    @Configuration("deprecated config")
    private val deprecated: Int by config(0)
    override fun visitClass(klass: KtClass) {
        // no-op
    }
}
