package io.gitlab.arturbosch.detekt.core.rules

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RuleSetSpec : Spek({

    describe("rule sets") {

        context("should rule set be used") {

            it("is explicitly deactivated") {
                val config = yamlConfig("configs/deactivated_ruleset.yml")
                assertThat(config.subConfig("comments").isActive()).isFalse()
            }

            it("is active with an empty config") {
                assertThat(Config.empty.isActive()).isTrue()
            }
        }

        context("should rule analyze a file") {

            val file by memoized { compileForTest(resourceAsPath("/cases/Default.kt")) }

            it("analyzes file with an empty config") {
                val config = Config.empty
                assertThat(config.subConfig("comments").shouldAnalyzeFile(file)).isTrue()
            }

            it("should not analyze file with *.kt excludes") {
                val config = TestConfig(Config.EXCLUDES_KEY to "**/*.kt")
                assertThat(config.subConfig("comments").shouldAnalyzeFile(file)).isFalse()
            }

            it("should analyze file as it's path is first excluded but then included") {
                val config = TestConfig(
                    Config.EXCLUDES_KEY to "**/*.kt",
                    Config.INCLUDES_KEY to "**/*.kt"
                )
                assertThat(config.subConfig("comments").shouldAnalyzeFile(file)).isTrue()
            }
        }
    }
})
