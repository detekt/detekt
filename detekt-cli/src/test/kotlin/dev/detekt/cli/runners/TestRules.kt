package dev.detekt.cli.runners

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtClass

class TestProvider : RuleSetProvider {
    override val ruleSetId = RuleSetId("test")
    override fun instance(): RuleSet =
        RuleSet(
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
            report(Finding(Entity.from(klass), description))
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
