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
    }

    @Nested
    inner class `auto correct config` {

        @Test
        fun `when specified it respects all autoCorrect values of rules and rule sets`() {
            val config = ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
                rules { autoCorrect = true }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }
            val style = config.subConfig("style")

            assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isTrue()
            assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
        }

        @Test
        fun `when not specified all autoCorrect values are overridden to false`() {
            val config = ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }
            val style = config.subConfig("style")

            assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isFalse()
            assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
        }

        @Test
        fun `when specified as false, all autoCorrect values are overridden to false`() {
            val config = ProcessingSpec {
                config { resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml")) }
                rules { autoCorrect = false }
            }.let { spec ->
                spec.workaroundConfiguration(spec.loadConfiguration())
            }
            val style = config.subConfig("style")

            assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isFalse()
            assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
        }

        @Test
        fun `regardless of other cli options, autoCorrect values are overridden to false`() {
            val config = ProcessingSpec {
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

            val style = config.subConfig("style")

            assertThat(style.subConfig("MagicNumber").valueOrNull<Boolean>("autoCorrect")).isFalse()
            assertThat(style.subConfig("MagicString").valueOrNull<Boolean>("autoCorrect")).isFalse()
        }
    }
}
