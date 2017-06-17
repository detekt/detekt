package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class CompositeConfigTest : Spek({

	describe("both configs should be considered") {
		val second = yamlConfig("composite-test.yml")
		val first = yamlConfig("detekt.yml")
		val compositeConfig = CompositeConfig(second, first)

		it("should have style sub config with active false which is overriden in `second` config") {
			val styleConfig = compositeConfig.subConfig("style").subConfig("WildcardImport")
			assertThat(styleConfig.valueOrDefault("active", true)).isEqualTo(false)
		}

		it("should have code smell sub config with LongMethod threshold 20 from `first` config") {
			val codeSmellConfig = compositeConfig.subConfig("code-smell").subConfig("LongMethod")
			assertThat(codeSmellConfig.valueOrDefault("threshold", -1)).isEqualTo(20)
		}
	}
})