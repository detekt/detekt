package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.resource
import org.jetbrains.kotlin.psi.KtClassOrObject
import java.nio.file.Path
import java.nio.file.Paths

val path: Path = Paths.get(resource("/cases"))

class TestProvider(override val ruleSetId: String = "Test") : RuleSetProvider {
    override fun instance(config: Config): RuleSet {
        return RuleSet("Test", listOf(FindName()))
    }
}

class TestProvider2(override val ruleSetId: String = "Test2") : RuleSetProvider {
    override fun instance(config: Config): RuleSet {
        return RuleSet("Test", listOf())
    }
}

class FindName : Rule() {
    override val issue: Issue = Issue(javaClass.simpleName, Severity.Minor, "", Debt.TWENTY_MINS)
    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        report(CodeSmell(issue, Entity.atName(classOrObject), message = ""))
    }
}
