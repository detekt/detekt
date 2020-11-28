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

    describe("build upon default config") {

        val config by memoized {
            ProcessingSpec {
                config {
                    resources = listOf(resourceUrl("/configs/fail-fast-wont-override-here.yml"))
                    useDefaultConfig = true
                }
                rules { activateExperimentalRules = true }
            }.loadConfiguration()
        }

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
})
