package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass

class TestProvider : RuleSetProvider {
    override val ruleSetId: String = "test"
    override fun instance(config: Config): RuleSet = RuleSet(ruleSetId, listOf(TestRule()))
}

class TestRule : Rule() {
    override val issue = Issue("test", Severity.Minor, "", Debt.FIVE_MINS)
    override fun visitClass(klass: KtClass) {
        if (klass.name == "Poko") {
            report(CodeSmell(issue, Entity.from(klass), issue.description))
        }
    }
}
