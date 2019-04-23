package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.ParameterException
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
internal class ConfigurationsSpec : Spek({

    describe("a configuration") {

        it("should be an empty config") {
            val config = CliArgs().loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(-1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(-1)
            assertThat(config.valueOrDefault("three", -1)).isEqualTo(-1)
        }
    }

    describe("parse different path based configuration settings") {
        val pathOne = resource("/configs/one.yml").path
        val pathTwo = resource("/configs/two.yml").path
        val pathThree = resource("/configs/three.yml").path

        it("should load single config") {
            val config = CliArgs { config = pathOne }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
        }

        it("should load two configs") {
            val config = CliArgs { config = "$pathOne, $pathTwo" }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
        }

        it("should load three configs") {
            val config = CliArgs { config = "$pathOne, $pathTwo;$pathThree" }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
            assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
        }

        it("should fail on invalid config value") {
            assertThatIllegalArgumentException().isThrownBy { CliArgs { config = "," }.loadConfiguration() }
            assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs { config = "sfsjfsdkfsd" }.loadConfiguration() }
            assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs { config = "./i.do.not.exist.yml" }.loadConfiguration() }
        }
    }

    describe("parse different resource based configuration settings") {

        it("should load single config") {
            val config = CliArgs { configResource = "/configs/one.yml" }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
        }

        it("should load two configs") {
            val config = CliArgs { configResource = "/configs/one.yml, /configs/two.yml" }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
        }

        it("should load three configs") {
            val config = CliArgs {
                configResource = "/configs/one.yml, /configs/two.yml;configs/three.yml"
            }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
            assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
        }

        it("should fail on invalid config value") {
            assertThatExceptionOfType(Config.InvalidConfigurationError::class.java).isThrownBy { CliArgs { configResource = "," }.loadConfiguration() }
            assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs { configResource = "sfsjfsdkfsd" }.loadConfiguration() }
            assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs { configResource = "./i.do.not.exist.yml" }.loadConfiguration() }
        }
    }

    describe("parse different filter settings") {

        it("should load single filter") {
            val filters = CliArgs { exclusions = "**/one/**" }.createFilters()
            assertThat(filters?.isIgnored(Paths.get("/one/path"))).isTrue()
            assertThat(filters?.isIgnored(Paths.get("/two/path"))).isFalse()
        }

        describe("parsing with different separators") {

            // can parse pattern **/one/**,**/two/**,**/three
            fun assertFilters(filters: PathFilters?) {
                assertThat(filters?.isIgnored(Paths.get("/one/path"))).isTrue()
                assertThat(filters?.isIgnored(Paths.get("/two/path"))).isTrue()
                assertThat(filters?.isIgnored(Paths.get("/three"))).isTrue()
                assertThat(filters?.isIgnored(Paths.get("/root/three"))).isTrue()
                assertThat(filters?.isIgnored(Paths.get("/three/path"))).isFalse()
            }

            it("should load multiple comma-separated filters with no spaces around commas") {
                val filters = CliArgs { exclusions = "**/one/**,**/two/**,**/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple semicolon-separated filters with no spaces around semicolons") {
                val filters = CliArgs { exclusions = "**/one/**;**/two/**;**/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple comma-separated filters with spaces around commas") {
                val filters = CliArgs { exclusions = "**/one/** ,**/two/**, **/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple semicolon-separated filters with spaces around semicolons") {
                val filters = CliArgs { exclusions = "**/one/** ;**/two/**; **/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple mixed-separated filters with no spaces around separators") {
                val filters = CliArgs { exclusions = "**/one/**,**/two/**;**/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple mixed-separated filters with spaces around separators") {
                val filters = CliArgs { exclusions = "**/one/** ,**/two/**; **/three" }.createFilters()
                assertFilters(filters)
            }

        }

        it("should ignore empty and blank filters") {
            val filters = CliArgs { exclusions = " ,,**/three" }.createFilters()
            assertThat(filters?.isIgnored(Paths.get("/three"))).isTrue()
            assertThat(filters?.isIgnored(Paths.get("/root/three"))).isTrue()
            assertThat(filters?.isIgnored(Paths.get("/one/path"))).isFalse()
            assertThat(filters?.isIgnored(Paths.get("/two/path"))).isFalse()
            assertThat(filters?.isIgnored(Paths.get("/three/path"))).isFalse()
        }
    }

    describe("fail fast only") {
        val config = CliArgs { configResource = "/configs/fail-fast-only.yml" }.loadConfiguration()

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
        val config = CliArgs { configResource = "/configs/fail-fast-override.yml" }.loadConfiguration()

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

    describe("build upon default config") {

        val config = CliArgs {
            buildUponDefaultConfig = true
            configResource = "/configs/no-fail-fast-override.yml"
        }.loadConfiguration()

        it("should override config when specified") {
            assertThat(config.subConfig("style").subConfig("MaxLineLength").valueOrDefault("maxLineLength", -1)).isEqualTo(100)
            assertThat(config.subConfig("style").subConfig("MaxLineLength").valueOrDefault("excludeCommentStatements", false)).isTrue()
        }

        it("should be active=false by default") {
            assertThat(config.subConfig("comments").subConfig("CommentOverPrivateFunction").valueOrDefault("active", true)).isFalse()
        }

        it("should be maxIssues=10 by default") {
            assertThat(config.subConfig("build").valueOrDefault("maxIssues", -1)).isEqualTo(10)
        }
    }
})
