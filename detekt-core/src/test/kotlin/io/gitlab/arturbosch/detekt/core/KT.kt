package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.psi.KtClassOrObject
import java.nio.file.Path

val path: Path = resourceAsPath("/cases")

class TestProvider : RuleSetProvider {
    override val ruleSetId: RuleSet.Id = RuleSet.Id("Test")

    override fun instance(): RuleSet {
        return RuleSet(ruleSetId, listOf(::FindName))
    }
}

class TestProvider2 : RuleSetProvider {
    override val ruleSetId: RuleSet.Id = RuleSet.Id("Test2")

    override fun instance(): RuleSet {
        return RuleSet(ruleSetId, emptyList())
    }
}

class FindName(config: Config) : Rule(config, "") {
    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        report(CodeSmell(Entity.atName(classOrObject), message = "TestMessage"))
    }
}
