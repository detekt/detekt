package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.ParameterException
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.PathFilter
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
internal class ConfigurationsSpec : Spek({

	it("should be an empty config") {
		val config = CliArgs().loadConfiguration()
		assertThat(config.valueOrDefault("one", -1)).isEqualTo(-1)
		assertThat(config.valueOrDefault("two", -1)).isEqualTo(-1)
		assertThat(config.valueOrDefault("three", -1)).isEqualTo(-1)
	}

	describe("parse different path based configuration settings") {
		val pathOne = resource("/configs/one.yml").path
		val pathTwo = resource("/configs/two.yml").path
		val pathThree = resource("/configs/three.yml").path

		it("should load single config") {
			val config = CliArgs().apply { config = pathOne }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
		}

		it("should load two configs") {
			val config = CliArgs().apply { config = "$pathOne, $pathTwo" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
		}

		it("should load three configs") {
			val config = CliArgs().apply { config = "$pathOne, $pathTwo;$pathThree" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
			assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
		}

		it("should fail on invalid config value") {
			assertThatIllegalArgumentException().isThrownBy { CliArgs().apply { config = "," }.loadConfiguration() }
			assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs().apply { config = "sfsjfsdkfsd" }.loadConfiguration() }
			assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs().apply { config = "./i.do.not.exist.yml" }.loadConfiguration() }
		}
	}

	describe("parse different resource based configuration settings") {

		it("should load single config") {
			val config = CliArgs().apply { configResource = "/configs/one.yml" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
		}

		it("should load two configs") {
			val config = CliArgs().apply { configResource = "/configs/one.yml, /configs/two.yml" }.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
		}

		it("should load three configs") {
			val config = CliArgs().apply {
				configResource = "/configs/one.yml, /configs/two.yml;configs/three.yml"
			}.loadConfiguration()
			assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
			assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
			assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
		}

		it("should fail on invalid config value") {
			assertThatExceptionOfType(Config.InvalidConfigurationError::class.java).isThrownBy { CliArgs().apply { configResource = "," }.loadConfiguration() }
			assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs().apply { configResource = "sfsjfsdkfsd" }.loadConfiguration() }
			assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs().apply { configResource = "./i.do.not.exist.yml" }.loadConfiguration() }
		}
	}

	describe("parse different filter settings") {

		it("should load single filter") {
			val filters = CliArgs().apply { filters = ".*/one/.*" }.createPathFilters()
			assertThat(filters).containsExactly(PathFilter(".*/one/.*"))
		}

		it("should load multiple comma-separated filters with no spaces around commas") {
			val filters = CliArgs().apply { filters = ".*/one/.*,.*/two/.*,.*/three" }.createPathFilters()
			assertThat(filters).containsExactly(
					PathFilter(".*/one/.*"),
					PathFilter(".*/two/.*"),
					PathFilter(".*/three")
			)
		}

		it("should load multiple semicolon-separated filters with no spaces around semicolons") {
			val filters = CliArgs().apply { filters = ".*/one/.*;.*/two/.*;.*/three" }.createPathFilters()
			assertThat(filters).containsExactly(
					PathFilter(".*/one/.*"),
					PathFilter(".*/two/.*"),
					PathFilter(".*/three")
			)
		}

		it("should load multiple comma-separated filters with spaces around commas") {
			val filters = CliArgs().apply { filters = ".*/one/.* ,.*/two/.*, .*/three" }.createPathFilters()
			assertThat(filters).containsExactly(
					PathFilter(".*/one/.*"),
					PathFilter(".*/two/.*"),
					PathFilter(".*/three")
			)
		}

		it("should load multiple semicolon-separated filters with spaces around semicolons") {
			val filters = CliArgs().apply { filters = ".*/one/.* ;.*/two/.*; .*/three" }.createPathFilters()
			assertThat(filters).containsExactly(
					PathFilter(".*/one/.*"),
					PathFilter(".*/two/.*"),
					PathFilter(".*/three")
			)
		}

		it("should load multiple mixed-separated filters with no spaces around separators") {
			val filters = CliArgs().apply { filters = ".*/one/.*,.*/two/.*;.*/three" }.createPathFilters()
			assertThat(filters).containsExactly(
					PathFilter(".*/one/.*"),
					PathFilter(".*/two/.*"),
					PathFilter(".*/three")
			)
		}

		it("should load multiple mixed-separated filters with spaces around separators") {
			val filters = CliArgs().apply { filters = ".*/one/.* ,.*/two/.*; .*/three" }.createPathFilters()
			assertThat(filters).containsExactly(
					PathFilter(".*/one/.*"),
					PathFilter(".*/two/.*"),
					PathFilter(".*/three")
			)
		}

		it("should ignore empty and blank filters") {
			val filters = CliArgs().apply { filters = " ,,.*/three" }.createPathFilters()
			assertThat(filters).containsExactly(PathFilter(".*/three"))
		}

		it("should fail on invalid filters values") {
			assertThatIllegalArgumentException().isThrownBy { CliArgs().apply { filters = "*." }.createPathFilters() }
			assertThatIllegalArgumentException().isThrownBy { CliArgs().apply { filters = "(ahel" }.createPathFilters() }
		}
	}

	describe("fail fast only") {
		val config = CliArgs().apply { configResource = "/configs/fail-fast-only.yml" }.loadConfiguration()

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
		val config = CliArgs().apply { configResource = "/configs/fail-fast-override.yml" }.loadConfiguration()

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
