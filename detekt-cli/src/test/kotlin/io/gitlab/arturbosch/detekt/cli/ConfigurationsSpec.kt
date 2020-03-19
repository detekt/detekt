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
            assertThatIllegalArgumentException()
                .isThrownBy { CliArgs { config = "," }.loadConfiguration() }
            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { CliArgs { config = "sfsjfsdkfsd" }.loadConfiguration() }
            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { CliArgs { config = "./i.do.not.exist.yml" }.loadConfiguration() }
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
            @Suppress("DEPRECATION")
            assertThatExceptionOfType(Config.InvalidConfigurationError::class.java)
                .isThrownBy { CliArgs { configResource = "," }.loadConfiguration() }
            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { CliArgs { configResource = "sfsjfsdkfsd" }.loadConfiguration() }
            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy { CliArgs { configResource = "./i.do.not.exist.yml" }.loadConfiguration() }
        }
    }

    describe("parse different filter settings") {

        it("should load single filter") {
            val filters = CliArgs { excludes = "**/one/**" }.createFilters()
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
                val filters = CliArgs { excludes = "**/one/**,**/two/**,**/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple semicolon-separated filters with no spaces around semicolons") {
                val filters = CliArgs { excludes = "**/one/**;**/two/**;**/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple comma-separated filters with spaces around commas") {
                val filters = CliArgs { excludes = "**/one/** ,**/two/**, **/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple semicolon-separated filters with spaces around semicolons") {
                val filters = CliArgs { excludes = "**/one/** ;**/two/**; **/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple mixed-separated filters with no spaces around separators") {
                val filters = CliArgs { excludes = "**/one/**,**/two/**;**/three" }.createFilters()
                assertFilters(filters)
            }

            it("should load multiple mixed-separated filters with spaces around separators") {
                val filters = CliArgs { excludes = "**/one/** ,**/two/**; **/three" }.createFilters()
                assertFilters(filters)
            }
        }

        it("should ignore empty and blank filters") {
            val filters = CliArgs { excludes = " ,,**/three" }.createFilters()
            assertThat(filters?.isIgnored(Paths.get("/three"))).isTrue()
            assertThat(filters?.isIgnored(Paths.get("/root/three"))).isTrue()
            assertThat(filters?.isIgnored(Paths.get("/one/path"))).isFalse()
            assertThat(filters?.isIgnored(Paths.get("/two/path"))).isFalse()
            assertThat(filters?.isIgnored(Paths.get("/three/path"))).isFalse()
        }
    }

    describe("fail fast only") {

        val config = CliArgs {
            configResource = "/configs/empty.yml"
            failFast = true
        }.loadConfiguration()

        it("should override active to true by default") {
            val actual = config.subConfig("comments")
                .subConfig("UndocumentedPublicClass")
                .valueOrDefault("active", false)
            assertThat(actual).isEqualTo(true)
        }

        it("should override maxIssues to 0 by default") {
            assertThat(config.subConfig("build").valueOrDefault("maxIssues", -1)).isEqualTo(0)
        }

        it("should keep config from default") {
            val actual = config.subConfig("style")
                .subConfig("MaxLineLength")
                .valueOrDefault("maxLineLength", -1)
            assertThat(actual).isEqualTo(120)
        }
    }

    describe("fail fast override") {

        val config = CliArgs {
            configResource = "/configs/fail-fast-will-override-here.yml"
            failFast = true
        }.loadConfiguration()

        it("should override config when specified") {
            val actual = config.subConfig("style")
                .subConfig("MaxLineLength")
                .valueOrDefault("maxLineLength", -1)
            assertThat(actual).isEqualTo(100)
        }

        it("should override active when specified") {
            val actual = config.subConfig("comments")
                .subConfig("CommentOverPrivateMethod")
                .valueOrDefault("active", true)
            assertThat(actual).isEqualTo(false)
        }

        it("should override maxIssues when specified") {
            assertThat(config.subConfig("build").valueOrDefault("maxIssues", -1)).isEqualTo(1)
        }
    }

    describe("build upon default config") {

        val config = CliArgs {
            buildUponDefaultConfig = true
            failFast = false
            configResource = "/configs/fail-fast-wont-override-here.yml"
        }.loadConfiguration()

        it("should override config when specified") {
            val ruleConfig = config.subConfig("style").subConfig("MaxLineLength")
            val lineLength = ruleConfig.valueOrDefault("maxLineLength", -1)
            val excludeComments = ruleConfig.valueOrDefault("excludeCommentStatements", false)

            assertThat(lineLength).isEqualTo(100)
            assertThat(excludeComments).isTrue()
        }

        it("should be active=false by default") {
            val actual = config.subConfig("comments")
                .subConfig("CommentOverPrivateFunction")
                .valueOrDefault("active", true)
            assertThat(actual).isFalse()
        }

        it("should be maxIssues=0 by default") {
            val actual = config.subConfig("build").valueOrDefault("maxIssues", -1)
            assertThat(actual).isEqualTo(0)
        }
    }

    describe("auto correct config") {

        context("when specified it respects all autoCorrect values of rules and rule sets") {
            val config = CliArgs {
                autoCorrect = true
                configResource = "/configs/config-with-auto-correct.yml"
            }.loadConfiguration()

            val style = config.subConfig("style")
            val comments = config.subConfig("comments")

            it("is disabled for rule sets") {
                assertThat(style.valueOrNull<Boolean>("autoCorrect")).isTrue()
                assertThat(comments.valueOrNull<Boolean>("autoCorrect")).isFalse()
            }

            it("is disabled for rules") {
                assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isTrue()
                assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.subConfig("ClassDoc").valueOrNull<Boolean>("autoCorrect")).isTrue()
                assertThat(comments.subConfig("FunctionDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
            }
        }

        mapOf(
            CliArgs {
                configResource = "/configs/config-with-auto-correct.yml"
            }.loadConfiguration() to "when not specified all autoCorrect values are overridden to false",
            CliArgs {
                autoCorrect = false
                configResource = "/configs/config-with-auto-correct.yml"
            }.loadConfiguration() to "when specified as false, all autoCorrect values are overridden to false",
            CliArgs {
                autoCorrect = false
                failFast = true
                buildUponDefaultConfig = true
                configResource = "/configs/config-with-auto-correct.yml"
            }.loadConfiguration() to "regardless of other cli options, autoCorrect values are overridden to false"
        ).forEach { (config, testContext) ->
            context(testContext) {
                val style = config.subConfig("style")
                val comments = config.subConfig("comments")

                it("is disabled for rule sets") {
                    assertThat(style.valueOrNull<Boolean>("autoCorrect")).isFalse()
                    assertThat(comments.valueOrNull<Boolean>("autoCorrect")).isFalse()
                }

                it("is disabled for rules") {
                    assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isFalse()
                    assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
                    assertThat(comments.subConfig("ClassDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
                    assertThat(comments.subConfig("FunctionDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
                }
            }
        }
    }
})
