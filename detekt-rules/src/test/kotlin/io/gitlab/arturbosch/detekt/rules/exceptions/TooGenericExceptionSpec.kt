package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.providers.ExceptionsProvider
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TooGenericExceptionSpec : Spek({

    describe("TooGenericException rule") {

        it("should not report any as all catch exception rules are deactivated") {
            val config = YamlConfig.loadResource(resource("deactivated-exceptions.yml").toURL())
            val ruleSet = ExceptionsProvider().buildRuleset(config)
            val file = compileForTest(Case.TooGenericExceptions.path())

            val findings = ruleSet?.accept(file)

            assertThat(findings).isEmpty()
        }
    }
})
