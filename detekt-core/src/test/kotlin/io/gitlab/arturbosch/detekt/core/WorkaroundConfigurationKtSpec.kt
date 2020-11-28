package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.resourceUrl
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.core.config.loadConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class WorkaroundConfigurationKtSpec : Spek({

    describe("with all rules activated by default") {

        val config by memoized {
            ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/empty.yml")) }
                rules { activateExperimentalRules = true }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }
        }

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

        val config by memoized {
            ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/fail-fast-will-override-here.yml")) }
                rules { activateExperimentalRules = true }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }
        }

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

    describe("auto correct config") {

        context("when specified it respects all autoCorrect values of rules and rule sets") {

            val config by memoized {
                ProcessingSpec {
                    config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
                    rules { autoCorrect = true }
                }.let { spec ->
                    spec.workaroundConfiguration(spec.loadConfiguration())
                }
            }

            val style by memoized { config.subConfig("style") }
            val comments by memoized { config.subConfig("comments") }

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
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            } to "when not specified all autoCorrect values are overridden to false",
            ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
                rules { autoCorrect = false }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            } to "when specified as false, all autoCorrect values are overridden to false",
            ProcessingSpec {
                config {
                    useDefaultConfig = true
                    resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml"))
                }
                rules {
                    autoCorrect = false
                    activateExperimentalRules = true
                }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            } to "regardless of other cli options, autoCorrect values are overridden to false"
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
