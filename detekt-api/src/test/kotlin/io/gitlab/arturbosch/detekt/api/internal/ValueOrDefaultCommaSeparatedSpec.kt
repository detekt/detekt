package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ValueOrDefaultCommaSeparatedSpec : Spek({

    describe("valueOrDefaultCommaSeparated") {

        it("returns the default when there is not a config value") {
            val config = TestConfig()

            assertThat(config.valueOrDefaultCommaSeparated("imports", listOf("java.utils.*")))
                .isEqualTo(listOf("java.utils.*"))
        }

        it("returns the default when there is a config String value") {
            val config = TestConfig("imports" to "butterknife.*,java.utils.*,")

            assertThat(config.valueOrDefaultCommaSeparated("imports", listOf("java.utils.*")))
                .isEqualTo(listOf("butterknife.*", "java.utils.*"))
        }

        it("returns the config value when there is a config List value") {
            val config = TestConfig("imports" to listOf("butterknife.*"))

            assertThat(config.valueOrDefaultCommaSeparated("imports", listOf("java.utils.*")))
                .isEqualTo(listOf("butterknife.*"))
        }
    }

    describe("valueOrDefaultCommaSeparated works with CompositeConfig") {

        it("and empty String") {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to "")),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList())).isEmpty()
        }

        it("and empty List") {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to emptyList<String>())),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList())).isEmpty()
        }

        it("and String with values") {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to "java.utils.*,butterknife.*")),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList()))
                .containsExactly("java.utils.*", "butterknife.*")
        }

        it("and List with values") {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to listOf("java.utils.*", "butterknife.*"))),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList()))
                .containsExactly("java.utils.*", "butterknife.*")
        }
    }
})
