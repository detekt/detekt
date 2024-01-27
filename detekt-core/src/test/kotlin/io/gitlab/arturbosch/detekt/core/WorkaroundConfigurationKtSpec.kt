package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.resourceUrl
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.core.config.loadConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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
        fun `when specified it respects all autoCorrect values of rules`() {
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

        @ParameterizedTest
        @CsvSource(
            """
                true, true
                true, false
                false, true
                false, false
            """
        )
        fun `regardless of other cli options, autoCorrect values are overridden to false`(
            useDefaultConfig: Boolean,
            activateAllRules: Boolean
        ) {
            val config = ProcessingSpec {
                config {
                    this.useDefaultConfig = useDefaultConfig
                    resources = listOf(resourceUrl("/configs/config-with-auto-correct.yml"))
                }
                rules {
                    autoCorrect = false
                    this.activateAllRules = activateAllRules
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
