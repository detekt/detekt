package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.test.yamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CompositeConfigTest : Spek({

    describe("both configs should be considered") {

        val second by memoized { yamlConfig("composite-test.yml") }
        val first by memoized { yamlConfig("detekt.yml") }
        val compositeConfig by memoized { CompositeConfig(second, first) }

        it("""
            should have style sub config with active false which is overridden
            in `second` config regardless of default value
        """) {
            val styleConfig = compositeConfig.subConfig("style").subConfig("WildcardImport")
            assertThat(styleConfig.valueOrDefault("active", true)).isEqualTo(false)
            assertThat(styleConfig.valueOrDefault("active", false)).isEqualTo(false)
        }

        it("should have code smell sub config with LongMethod threshold 20 from `first` config") {
            val codeSmellConfig = compositeConfig.subConfig("code-smell").subConfig("LongMethod")
            assertThat(codeSmellConfig.valueOrDefault("threshold", -1)).isEqualTo(20)
        }

        it("should use the default as both part configurations do not have the value") {
            assertThat(compositeConfig.valueOrDefault("TEST", 42)).isEqualTo(42)
        }

        it("should return a string based on default value") {
            val config = compositeConfig.subConfig("style").subConfig("MagicNumber")
            val value = config.valueOrDefault("ignoreNumbers", emptyList<String>())
            assertThat(value).isEqualTo(listOf("-1", "0", "1", "2", "100", "1000"))
        }
    }
})
