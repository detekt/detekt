package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.psi.KtClassOrObject
import java.nio.file.Path

val path: Path = resourceAsPath("/cases")

class TestProvider(override val ruleSetId: String = "Test") : RuleSetProvider {
    override fun instance(config: Config): RuleSet {
        return RuleSet("Test", listOf(FindName(config)))
    }
}

class TestProvider2(override val ruleSetId: String = "Test2") : RuleSetProvider {
    override fun instance(config: Config): RuleSet {
        return RuleSet("Test", emptyList())
    }
}

class FindName(config: Config) : Rule(config) {
    override val issue: Issue = Issue(javaClass.simpleName, "")
    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        report(CodeSmell(issue, Entity.atName(classOrObject), message = ""))
    }
}
