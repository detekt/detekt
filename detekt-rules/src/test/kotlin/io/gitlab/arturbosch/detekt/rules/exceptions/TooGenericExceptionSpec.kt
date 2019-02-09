package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.providers.ExceptionsProvider
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class TooGenericExceptionSpec : Spek({

    it("should not report any as all catch exception rules are deactivated") {
        val config = YamlConfig.loadResource(resource("deactivated-exceptions.yml").toURL())
        val ruleSet = ExceptionsProvider().buildRuleset(config)
        val file = compileForTest(Case.TooGenericExceptions.path())

        val findings = ruleSet?.accept(file)

        assertThat(findings).hasSize(0)
    }
})
