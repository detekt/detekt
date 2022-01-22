package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValueOrDefaultCommaSeparatedSpec {

    @Nested
    inner class `valueOrDefaultCommaSeparated` {

        @Test
        fun `returns the default when there is not a config value`() {
            val config = TestConfig()

            assertThat(config.valueOrDefaultCommaSeparated("imports", listOf("java.utils.*")))
                .isEqualTo(listOf("java.utils.*"))
        }

        @Test
        fun `returns the default when there is a config String value`() {
            val config = TestConfig("imports" to "butterknife.*,java.utils.*,")

            assertThat(config.valueOrDefaultCommaSeparated("imports", listOf("java.utils.*")))
                .isEqualTo(listOf("butterknife.*", "java.utils.*"))
        }

        @Test
        fun `returns the config value when there is a config List value`() {
            val config = TestConfig("imports" to listOf("butterknife.*"))

            assertThat(config.valueOrDefaultCommaSeparated("imports", listOf("java.utils.*")))
                .isEqualTo(listOf("butterknife.*"))
        }
    }

    @Nested
    inner class `valueOrDefaultCommaSeparated works with CompositeConfig` {

        @Test
        fun `and empty String`() {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to "")),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList())).isEmpty()
        }

        @Test
        fun `and empty List`() {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to emptyList<String>())),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList())).isEmpty()
        }

        @Test
        fun `and String with values`() {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to "java.utils.*,butterknife.*")),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList()))
                .containsExactly("java.utils.*", "butterknife.*")
        }

        @Test
        fun `and List with values`() {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to listOf("java.utils.*", "butterknife.*"))),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList()))
                .containsExactly("java.utils.*", "butterknife.*")
        }
    }
}
