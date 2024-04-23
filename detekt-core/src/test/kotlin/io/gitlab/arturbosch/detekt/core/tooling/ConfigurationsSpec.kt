package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.test.utils.resourceUrl
import io.github.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ConfigurationsSpec {

    @Nested
    inner class `a configuration` {

        @Test
        fun `should be an empty config`() {
            val config = ProcessingSpec {}.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(-1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(-1)
            assertThat(config.valueOrDefault("three", -1)).isEqualTo(-1)
        }
    }

    @Nested
    inner class `parse different path based configuration settings` {

        val pathOne = resourceAsPath("/configs/one.yml")
        val pathTwo = resourceAsPath("/configs/two.yml")
        val pathThree = resourceAsPath("/configs/three.yml")

        @Test
        fun `should load single config`() {
            val config = ProcessingSpec {
                config { configPaths = listOf(pathOne) }
            }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
        }

        @Test
        fun `should load two configs`() {
            val config = ProcessingSpec {
                config { configPaths = listOf(pathOne, pathTwo) }
            }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
        }

        @Test
        fun `should load three configs`() {
            val config = ProcessingSpec {
                config { configPaths = listOf(pathOne, pathTwo, pathThree) }
            }.loadConfiguration()
            assertThat(config.valueOrDefault("one", -1)).isEqualTo(1)
            assertThat(config.valueOrDefault("two", -1)).isEqualTo(2)
            assertThat(config.valueOrDefault("three", -1)).isEqualTo(3)
        }
    }

    @Nested
    inner class `parse different resource based configuration settings` {

        @Test
        fun `should load three configs`() {
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
}
