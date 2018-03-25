package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFails

/**
 * @author Artur Bosch
 */
internal class ConfigurationsSpec : Spek({

	it("should be an empty config") {
		val config = Args().loadConfiguration()
		assertThat(config.valueOrDefault("one", -1)).isEqualTo(-1)
		assertThat(config.valueOrDefault("two", -1)).isEqualTo(-1)
		assertThat(config.valueOrDefault("three", -1)).isEqualTo(-1)
	}

	describe("parse different path based configuration settings") {
		val pathOne = resource("/configs/one.yml").path
		val pathTwo = resource("/configs/two.yml").path
		val pathThree = resource("/configs/three.yml").path

		it("should load single config") {
			val config = Args().apply { config = pathOne }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
		}

		it("should load two configs") {
			val config = Args().apply { config = "$pathOne, $pathTwo" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
		}

		it("should load three configs") {
			val config = Args().apply { config = "$pathOne, $pathTwo;$pathThree" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
			assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
		}

		it("should fail on invalid config value") {
			assertFails { Args().apply { config = "," }.loadConfiguration() }
			assertFails { Args().apply { config = "sfsjfsdkfsd" }.loadConfiguration() }
			assertFails { Args().apply { config = "./i.do.not.exist.yml" }.loadConfiguration() }
		}
	}

	describe("parse different resource based configuration settings") {

		it("should load single config") {
			val config = Args().apply { configResource = "/configs/one.yml" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
		}

		it("should load two configs") {
			val config = Args().apply { configResource = "/configs/one.yml, /configs/two.yml" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
		}

		it("should load three configs") {
			val config = Args().apply {
				configResource = "/configs/one.yml, /configs/two.yml;configs/three.yml"
			}.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
			assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
		}

		it("should fail on invalid config value") {
			assertFails { Args().apply { configResource = "," }.loadConfiguration() }
			assertFails { Args().apply { configResource = "sfsjfsdkfsd" }.loadConfiguration() }
			assertFails { Args().apply { configResource = "./i.do.not.exist.yml" }.loadConfiguration() }
		}
	}

	describe("fail fast only") {
		val config = Args().apply { configResource = "/configs/fail-fast-only.yml" }.loadConfiguration()

		it("should override active to true by default") {
			assertThat(config.subConfig("comments").subConfig("UndocumentedPublicClass").valueOrDefault("active", false)).isEqualTo(true)
		}

		it("should override maxIssues to 0 by default") {
			assertThat(config.subConfig("build").valueOrDefault("maxIssues", -1)).isEqualTo(0)
		}

		it("should keep config from default") {
			assertThat(config.subConfig("style").subConfig("MaxLineLength").valueOrDefault("maxLineLength", -1)).isEqualTo(120)
		}
	}

	describe("fail fast override") {
		val config = Args().apply { configResource = "/configs/fail-fast-override.yml" }.loadConfiguration()

		it("should override config when specified") {
			assertThat(config.subConfig("style").subConfig("MaxLineLength").valueOrDefault("maxLineLength", -1)).isEqualTo(100)
		}

		it("should override active when specified") {
			assertThat(config.subConfig("comments").subConfig("CommentOverPrivateMethod").valueOrDefault("active", true)).isEqualTo(false)
		}

		it("should override maxIssues when specified") {
			assertThat(config.subConfig("build").valueOrDefault("maxIssues", -1)).isEqualTo(1)
		}
	}

})
