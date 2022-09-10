package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.resourceUrl
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.core.config.loadConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WorkaroundConfigurationKtSpec {

    @Nested
    inner class `with all rules activated by default` {

        private val config = ProcessingSpec {
            config { resources = listOf(resourceUrl("/configs/empty.yml")) }
            rules { activateAllRules = true }
        }.let { spec ->
            spec.workaroundConfiguration(spec.loadConfiguration())
        }

        @Test
        fun `should override active to true by default`() {
            val actual = config.subConfig("comments")
                .subConfig("UndocumentedPublicClass")
                .valueOrDefault("active", false)
            assertThat(actual).isEqualTo(true)
        }

        @Test
        fun `should override maxIssues to 0 by default`() {
            assertThat(config.subConfig("build").valueOrDefault("maxIssues", -1)).isEqualTo(0)
        }

        @Test
        fun `should keep config from default`() {
            val actual = config.subConfig("style")
                .subConfig("MaxLineLength")
                .valueOrDefault("maxLineLength", -1)
            assertThat(actual).isEqualTo(120)
        }
    }

    @Nested
    inner class `activate all rules override` {

        private val config = ProcessingSpec {
            config { resources = listOf(resourceUrl("/configs/activate-all-rules-will-override-here.yml")) }
            rules { activateAllRules = true }
        }.let { spec ->
            spec.workaroundConfiguration(spec.loadConfiguration())
        }

        @Test
        fun `should override config when specified`() {
            val actual = config.subConfig("style")
                .subConfig("MaxLineLength")
                .valueOrDefault("maxLineLength", -1)
            assertThat(actual).isEqualTo(100)
        }

        @Test
        fun `should override active when specified`() {
            val actual = config.subConfig("comments")
                .subConfig("CommentOverPrivateMethod")
                .valueOrDefault("active", true)
            assertThat(actual).isEqualTo(false)
        }

        @Test
        fun `should override maxIssues when specified`() {
            assertThat(config.subConfig("build").valueOrDefault("maxIssues", -1)).isEqualTo(1)
        }
    }

    @Nested
    inner class `auto correct config` {

        @Nested
        inner class `when specified it respects all autoCorrect values of rules and rule sets` {

            private val config = ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
                rules { autoCorrect = true }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }

            private val style = config.subConfig("style")
            private val comments = config.subConfig("comments")

            @Test
            fun `is disabled for rule sets`() {
                assertThat(style.valueOrNull<Boolean>("autoCorrect")).isTrue()
                assertThat(comments.valueOrNull<Boolean>("autoCorrect")).isFalse()
            }

            @Test
            fun `is disabled for rules`() {
                assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isTrue()
                assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.subConfig("ClassDoc").valueOrNull<Boolean>("autoCorrect")).isTrue()
                assertThat(comments.subConfig("FunctionDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
            }
        }

        @Nested
        inner class `when not specified all autoCorrect values are overridden to false` {
            private val config = ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }
            private val style = config.subConfig("style")
            private val comments = config.subConfig("comments")

            @Test
            fun `is disabled for rule sets`() {
                assertThat(style.valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.valueOrNull<Boolean>("autoCorrect")).isFalse()
            }

            @Test
            fun `is disabled for rules`() {
                assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.subConfig("ClassDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.subConfig("FunctionDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
            }
        }

        @Nested
        inner class `when specified as false, all autoCorrect values are overridden to false` {
            private val config = ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
                rules { autoCorrect = false }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }
            private val style = config.subConfig("style")
            private val comments = config.subConfig("comments")

            @Test
            fun `is disabled for rule sets`() {
                assertThat(style.valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.valueOrNull<Boolean>("autoCorrect")).isFalse()
            }

            @Test
            fun `is disabled for rules`() {
                assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.subConfig("ClassDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.subConfig("FunctionDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
            }
        }

        @Nested
        inner class `regardless of other cli options, autoCorrect values are overridden to false` {
            private val config = ProcessingSpec {
                config {
                    useDefaultConfig = true
                    resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml"))
                }
                rules {
                    autoCorrect = false
                    activateAllRules = true
                }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }

            private val style = config.subConfig("style")
            private val comments = config.subConfig("comments")

            @Test
            fun `is disabled for rule sets`() {
                assertThat(style.valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.valueOrNull<Boolean>("autoCorrect")).isFalse()
            }

            @Test
            fun `is disabled for rules`() {
                assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.subConfig("ClassDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
                assertThat(comments.subConfig("FunctionDoc").valueOrNull<Boolean>("autoCorrect")).isFalse()
            }
        }
    }
}
