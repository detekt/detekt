package io.gitlab.arturbosch.detekt.api

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Paths
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * @author Artur Bosch
 */
class ConfigSpec : Spek({

	describe("load yaml config") {
		val configPath = Paths.get(ConfigSpec::class.java.getResource("/detekt.yml").path)
		val config = YamlConfig.load(configPath)

		it("should create a sub config") {
			try {
				val subConfig = config.subConfig("style")
				assertTrue { subConfig.valueOrDefault("WildcardImport", { mapOf<String, Any>() }).isNotEmpty() }
				assertTrue { subConfig.valueOrDefault("WildcardImport", { mapOf<String, Any>() })["active"].toString() == "true" }
				assertTrue { subConfig.valueOrDefault("WildcardImport", { mapOf<String, Any>() })["active"] as Boolean }
				assertTrue { subConfig.valueOrDefault("NotFound", { mapOf<String, Any>() }).isEmpty() }
				assertTrue { subConfig.valueOrDefault("NotFound", { "" }) == "" }
			} catch (ignored: Config.InvalidConfigurationError) {
				fail("Creating a sub config should work for test resources config!")
			}
		}

		it("should create a sub sub config") {
			try {
				val subConfig = config.subConfig("style")
				val subSubConfig = subConfig.subConfig("WildcardImport")
				assertTrue { subSubConfig.valueOrDefault("active") { false } }
				assertTrue { subSubConfig.valueOrDefault("NotFound") { true } }
			} catch (ignored: Config.InvalidConfigurationError) {
				fail("Creating a sub config should work for test resources config!")
			}
		}

		it("tests wrong sub config conversion") {
			assertFailsWith<ClassCastException> {
				@Suppress("UNUSED_VARIABLE")
				val ignored = config.valueOrDefault("style") { "" }
			}
		}
	}

})