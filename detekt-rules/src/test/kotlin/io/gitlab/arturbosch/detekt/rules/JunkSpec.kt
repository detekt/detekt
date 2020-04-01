package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.internal.CompositeConfig
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class JunkSpec : Spek({

    describe("valueOrDefaultCommaSeparated works with CompositeConfig") {

        it("and empty String") {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to "")),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList(), "")).isEmpty()
        }

        it("and empty List") {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to emptyList<String>())),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList(), "")).isEmpty()
        }

        it("and String with values") {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to "java.utils.*,butterknife.*")),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList(), ""))
                .containsExactly("java.utils.*", "butterknife.*")
        }

        it("and List with values") {
            val config = CompositeConfig(
                TestConfig(mapOf("imports" to listOf("java.utils.*", "butterknife.*"))),
                TestConfig(mapOf("imports" to emptyList<String>()))
            )

            assertThat(config.valueOrDefaultCommaSeparated("imports", emptyList(), ""))
                .containsExactly("java.utils.*", "butterknife.*")
        }
    }
})
