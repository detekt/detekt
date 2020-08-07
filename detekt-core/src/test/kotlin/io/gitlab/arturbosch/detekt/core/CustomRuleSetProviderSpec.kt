package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.rules.RuleSetLocator
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * This test runs a precompiled jar with a custom rule provider.
 * When any breaking change in 'detekt-api' is done, this test will break.
 *
 * The procedure to repair this test is:
 *  1. 'gradle build -x test publishToMavenLocal'
 *  2. 'gradle build' again to let the 'sample' project pick up the new api changes.
 *  3. 'cp detekt-sample-extensions/build/libs/detekt-sample-extensions-<version>.jar
 *          detekt-core/src/test/resources/sample-rule-set.jar'
 *  4. Now 'gradle build' should be green again.
 */
class CustomRuleSetProviderSpec : Spek({

    describe("custom rule sets should be loadable through jars") {

        it("should load the sample provider") {
            val sampleRuleSet = resourceAsPath("sample-rule-set.jar")
            val spec = createNullLoggingSpec {
                extensions {
                    disableDefaultRuleSets = true
                    fromPaths { listOf(sampleRuleSet) }
                }
            }

            val providers = spec.withSettings { RuleSetLocator(this).load() }

            assertThat(providers).filteredOn { it.ruleSetId == "sample" }.hasSize(1)
        }
    }
})
