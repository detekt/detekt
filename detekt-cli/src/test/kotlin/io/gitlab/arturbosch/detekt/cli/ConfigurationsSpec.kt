package io.gitlab.arturbosch.detekt.cli

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
		val config = Main().loadConfiguration()
		assertThat(config.valueOrDefault("one", -1)).isEqualTo(-1)
		assertThat(config.valueOrDefault("two", -1)).isEqualTo(-1)
		assertThat(config.valueOrDefault("three", -1)).isEqualTo(-1)
	}

	describe("parse different path based configuration settings") {
		val pathOne = javaClass.getResource("/configs/one.yml").path
		val pathTwo = javaClass.getResource("/configs/two.yml").path
		val pathThree = javaClass.getResource("/configs/three.yml").path

		it("should load single config") {
			val config = Main().apply { config = pathOne }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
		}

		it("should load two configs") {
			val config = Main().apply { config = "$pathOne, $pathTwo" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
		}

		it("should load three configs") {
			val config = Main().apply { config = "$pathOne, $pathTwo;$pathThree" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
			assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
		}

		it("should fail on invalid config value") {
			assertFails { Main().apply { config = "," }.loadConfiguration() }
			assertFails { Main().apply { config = "sfsjfsdkfsd" }.loadConfiguration() }
			assertFails { Main().apply { config = "./i.do.not.exist.yml" }.loadConfiguration() }
		}
	}

	describe("parse different resource based configuration settings") {

		it("should load single config") {
			val config = Main().apply { configResource = "/configs/one.yml" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
		}

		it("should load two configs") {
			val config = Main().apply { configResource = "/configs/one.yml, /configs/two.yml" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
		}

		it("should load three configs") {
			val config = Main().apply {
				configResource = "/configs/one.yml, /configs/two.yml;configs/three.yml"
			}.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
			assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
		}

		it("should fail on invalid config value") {
			assertFails { Main().apply { configResource = "," }.loadConfiguration() }
			assertFails { Main().apply { configResource = "sfsjfsdkfsd" }.loadConfiguration() }
			assertFails { Main().apply { configResource = "./i.do.not.exist.yml" }.loadConfiguration() }
		}
	}
})