package io.gitlab.arturbosch.detekt.cli

import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import com.beust.jcommander.ParameterException
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.test.*
import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class ConfigurationsSpec : Spek({


    describe("a configuration") {

        it("should be an empty config") {
            val config = CliArgs().loadConfiguration()
            expect(config) {
                hasNotKey("one")
                hasNotKey("two")
                hasNotKey("three")
            }
        }

        it("fail on purpose with AssertJ - should be an empty config") {
            val config = CliArgs().loadConfiguration()
            Assertions.assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            Assertions.assertThat(config.valueOrDefault("two", -1)).isEqualTo(1)
            Assertions.assertThat(config.valueOrDefault("three", -1)).isEqualTo(1)
        }

        it("fail on purpose with Atrium - should be an empty config") {
            val config = CliArgs().loadConfiguration()
            expect(config) {
                hasKey("one")
                hasKey("two")
                hasKey("three")
            }
        }

    }

    describe("parse different path based configuration settings") {
        val pathOne = resource("/configs/one.yml").path
        val pathTwo = resource("/configs/two.yml").path
        val pathThree = resource("/configs/three.yml").path

        it("should load single config") {
            val config = CliArgs { config = pathOne }.loadConfiguration()
            expect(config) {
                hasKeyValue("one", 1)
            }
        }

        it("should load two configs") {
            val config = CliArgs { config = "$pathOne, $pathTwo" }.loadConfiguration()
            expect(config) {
                hasKeyValue("one", 1)
                hasKeyValue("two", 2)
            }
        }

        it("should load three configs") {
            val config = CliArgs { config = "$pathOne, $pathTwo;$pathThree" }.loadConfiguration()
            expect(config) {
                hasKeyValue("one", 1)
                hasKeyValue("two", 2)
                hasKeyValue("three", 3)
            }
        }

        it("should fail on invalid config value") {
            expect { CliArgs { config = "," }.loadConfiguration() }.toThrow<IllegalArgumentException>()
            expect { CliArgs { config = "sfsjfsdkfsd" }.loadConfiguration() }.toThrow<ParameterException>()
            expect { CliArgs { config = "./i.do.not.exist.yml" }.loadConfiguration() }.toThrow<ParameterException>()
        }



        it("fail on purpose with AssertJ - should load three configs") {
            val config = CliArgs { config = "$pathOne, $pathTwo;$pathThree" }.loadConfiguration()
            Assertions.assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            Assertions.assertThat(config.valueOrDefault("two", -1)).isEqualTo(200)
            Assertions.assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
        }

        it("fail on purpose with Atrium -should load three configs") {
            val config = CliArgs { config = "$pathOne, $pathTwo;$pathThree" }.loadConfiguration()
            expect(config) {
                hasKeyValue("one", 1)
                hasKeyValue("two", 200)
                hasKeyValue("three", 3)
            }
        }

        it("fail on purpose with AssertJ - should fail on invalid config value") {
            Assertions.assertThatExceptionOfType(ParameterException::class.java).isThrownBy { CliArgs { config = "," }.loadConfiguration() }
        }

        it("fail on purpose with Atrium - should fail on invalid config value") {
            expect { CliArgs { config = "," }.loadConfiguration() }.toThrow<ParameterException>()
        }
    }

    describe("parse different resource based configuration settings") {

        it("should load single config") {
            val config = CliArgs { configResource = "/configs/one.yml" }.loadConfiguration()
            expect(config).hasKeyValue("one", 1)
        }

        it("should load two configs") {
            val config = CliArgs { configResource = "/configs/one.yml, /configs/two.yml" }.loadConfiguration()
            expect(config) {
                hasKeyValue("one", 1)
                hasKeyValue("two", 2)
            }
        }

        it("should load three configs") {
            val config = CliArgs {
                configResource = "/configs/one.yml, /configs/two.yml;configs/three.yml"
            }.loadConfiguration()
            expect(config) {
                hasKeyValue("one", 1)
                hasKeyValue("two", 2)
                hasKeyValue("three", 3)
            }
        }

        it("should fail on invalid config value") {
            expect { CliArgs { configResource = "," }.loadConfiguration() }.toThrow<Config.InvalidConfigurationError>()
            expect { CliArgs { configResource = "sfsjfsdkfsd" }.loadConfiguration() }.toThrow<ParameterException>()
            expect { CliArgs { configResource = "./i.do.not.exist.yml" }.loadConfiguration() }.toThrow<ParameterException>()
        }
    }

    describe("parse different filter settings") {

        it("should load single filter") {
            val filters = CliArgs { excludes = "**/one/**" }.createFilters()
            expect(filters).isIgnored("/one/path")
            expect(filters).isNotIgnored("/two/path")
        }

        describe("parsing with different separators") {

            // can parse pattern **/one/**,**/two/**,**/three
            fun assertFilters(filters: PathFilters?) {
                expect(filters).isIgnored("/one/path")
                expect(filters).isIgnored("/two/path")
                expect(filters).isIgnored("/three")
                expect(filters).isIgnored("/root/three")
                expect(filters).isNotIgnored("/three/path")
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
            expect(filters).isIgnored("/three")
            expect(filters).isIgnored("/root/three")
            expect(filters).isNotIgnored("/one/path")
            expect(filters).isNotIgnored("/two/path")
            expect(filters).isNotIgnored("/three/path")
        }
    }

    describe("fail fast only") {
        val config = CliArgs {
            configResource = "/configs/empty.yml"
            failFast = true
        }.loadConfiguration()

        it("should override active to true by default") {
            expect(config.subConfig("comments").subConfig("UndocumentedPublicClass")).hasKeyValue("active", true)
        }

        it("should override maxIssues to 0 by default") {
            expect(config.subConfig("build")).hasKeyValue("maxIssues", 0)
        }

        it("should keep config from default") {
            expect(config.subConfig("style").subConfig("MaxLineLength")).hasKeyValue("maxLineLength", 120)
        }
    }

    describe("fail fast override") {
        val config = CliArgs {
            configResource = "/configs/fail-fast-will-override-here.yml"
            failFast = true
        }.loadConfiguration()

        it("should override config when specified") {
            expect(config.subConfig("style").subConfig("MaxLineLength")).hasKeyValue("maxLineLength", 100)
        }

        it("should override active when specified") {
            expect(config.subConfig("comments").subConfig("CommentOverPrivateMethod")).hasKeyValue("active", false)
        }

        it("should override maxIssues when specified") {
            expect(config.subConfig("build")).hasKeyValue("maxIssues", 1)
        }
    }

    describe("build upon default config") {

        val config = CliArgs {
            buildUponDefaultConfig = true
            failFast = false
            configResource = "/configs/fail-fast-wont-override-here.yml"
        }.loadConfiguration()

        it("should override config when specified") {
            expect(config.subConfig("style").subConfig("MaxLineLength")).hasKeyValue("maxLineLength", 100)
            expect(config.subConfig("style").subConfig("MaxLineLength")).hasKeyValue("excludeCommentStatements", true)
        }

        it("should be active=false by default") {
            expect(config.subConfig("comments").subConfig("CommentOverPrivateFunction")).hasKeyValue("active", false)
        }

        it("should be maxIssues=0 by default") {
            expect(config.subConfig("build")).hasKeyValue("maxIssues", 0)
        }
    }
})
