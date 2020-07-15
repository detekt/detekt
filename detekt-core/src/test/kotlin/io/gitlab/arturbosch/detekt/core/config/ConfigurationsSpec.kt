package io.gitlab.arturbosch.detekt.core.config

import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.test.utils.resourceUrl
import io.github.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class ConfigurationsSpec : Spek({

    describe("a configuration") {

        it("should be an empty config") {
            val config = ProcessingSpec {}.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(-1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(-1)
            assertThat(config.valueOrDefault("three", -1)).isEqualTo(-1)
        }
    }

    describe("parse different path based configuration settings") {
        val pathOne = resourceAsPath("/configs/one.yml")
        val pathTwo = resourceAsPath("/configs/two.yml")
        val pathThree = resourceAsPath("/configs/three.yml")

        it("should load single config") {
            val config = ProcessingSpec {
                config { configPaths = listOf(pathOne) }
            }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
        }

        it("should load two configs") {
            val config = ProcessingSpec {
                config { configPaths = listOf(pathOne, pathTwo) }
            }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
        }

        it("should load three configs") {
            val config = ProcessingSpec {
                config { configPaths = listOf(pathOne, pathTwo, pathThree) }
            }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
            assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
        }
    }

    describe("parse different resource based configuration settings") {

        it("should load three configs") {
            val config = ProcessingSpec {
                config {
                    resources = listOf(
                        resourceUrl("/configs/one.yml"),
                        resourceUrl("/configs/two.yml"),
                        resourceUrl("/configs/three.yml")
                    )
                }
            }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
            assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
        }
    }

    describe("with all rules activated by default") {

        val config = ProcessingSpec {
            config { resources = listOf(resourceUrl("/configs/empty.yml")) }
            rules { activateExperimentalRules = true }
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

        val config = ProcessingSpec {
            config { resources = listOf(resourceUrl("/configs/fail-fast-will-override-here.yml")) }
            rules { activateExperimentalRules = true }
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

        val config = ProcessingSpec {
            config {
                resources = listOf(resourceUrl("/configs/fail-fast-wont-override-here.yml"))
                useDefaultConfig = true
            }
            rules { activateExperimentalRules = true }
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

            val config = ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
                rules { autoCorrect = true }
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
            ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
            }.loadConfiguration() to "when not specified all autoCorrect values are overridden to false",
            ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
                rules { autoCorrect = false }
            }.loadConfiguration() to "when specified as false, all autoCorrect values are overridden to false",
            ProcessingSpec {
                config {
                    useDefaultConfig = true
                    resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml"))
                }
                rules {
                    autoCorrect = false
                    activateExperimentalRules = true
                }
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
