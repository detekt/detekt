package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.test.createProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

/**
 * This test runs a precompiled jar with a custom rule provider.
 * When any breaking change in 'detekt-api' is done, this test will break.
 *
 * The procedure to repair this test is:
 *  1. 'gradle build -x test publishToMavenLocal'
 *  2. 'gradle build' again to let the 'sample' project pick up the new api changes.
 *  3. 'cp detekt-sample-extensions/build/libs/detekt-sample-extensions-<version>.jar detekt-core/src/test/resources/sample-rule-set.jar'
 *  4. Now 'gradle build' should be green again.
 */
@Suppress("MaxLineLength")
class CustomRuleSetProviderSpec : Spek({

    describe("custom rule sets should be loadable through jars") {

        val sampleRuleSet = Paths.get(resource("sample-rule-set.jar"))

        it("should load the sample provider") {
            val providers = createProcessingSettings(
                path,
                excludeDefaultRuleSets = true,
                pluginPaths = listOf(sampleRuleSet)
            ).use { RuleSetLocator(it).load() }

            assertThat(providers).filteredOn { it.ruleSetId == "sample" }.hasSize(1)
        }
    }
})
