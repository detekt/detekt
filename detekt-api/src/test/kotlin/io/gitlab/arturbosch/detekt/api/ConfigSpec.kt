package io.gitlab.arturbosch.detekt.api

import java.nio.file.Paths
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.fail
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class ConfigSpec : Spek({

    describe("load yaml config") {
        val configPath = Paths.get(resource("/detekt.yml"))
        val config = YamlConfig.load(configPath)

        it("should create a sub config") {
            try {
                val subConfig = config.subConfig("style")
                assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())).isNotEmpty
                assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())["active"].toString()).isEqualTo("true")
                assertThat(subConfig.valueOrDefault("WildcardImport", mapOf<String, Any>())["active"] as Boolean).isTrue()
                assertThat(subConfig.valueOrDefault("NotFound", mapOf<String, Any>())).isEmpty()
                assertThat(subConfig.valueOrDefault("NotFound", "")).isEmpty()
            } catch (ignored: Config.InvalidConfigurationError) {
                fail("Creating a sub config should work for test resources config!")
            }
        }

        it("should create a sub sub config") {
            try {
                val subConfig = config.subConfig("style")
                val subSubConfig = subConfig.subConfig("WildcardImport")
                assertThat(subSubConfig.valueOrDefault("active", false)).isTrue()
                assertThat(subSubConfig.valueOrDefault("NotFound", true)).isTrue()
            } catch (ignored: Config.InvalidConfigurationError) {
                fail("Creating a sub config should work for test resources config!")
            }
        }

        it("tests wrong sub config conversion") {
            assertThatExceptionOfType(ClassCastException::class.java).isThrownBy {
                @Suppress("UNUSED_VARIABLE")
                val ignored = config.valueOrDefault("style", "")
            }
        }
    }

    describe("loading empty configurations") {

        it("empty yaml file is equivalent to empty config") {
            YamlConfig.loadResource(javaClass.getResource("/empty.yml"))
        }

        it("single item in yaml file is valid") {
            YamlConfig.loadResource(javaClass.getResource("/oneitem.yml"))
        }
    }
})
