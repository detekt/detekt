package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.core.config.DefaultConfig
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DetektYmlConfigSpec : Spek({

    describe("detekt YAML config") {

        val config by memoized { DefaultConfig.newInstance() }

        it("complexitySection") {
            ConfigAssert(
                config,
                "complexity",
                "io.gitlab.arturbosch.detekt.rules.complexity"
            ).assert()
        }

        it("documentationSection") {
            ConfigAssert(
                config,
                "comments",
                "io.gitlab.arturbosch.detekt.rules.documentation"
            ).assert()
        }

        it("emptyBlocksSection") {
            ConfigAssert(
                config,
                "empty-blocks",
                "io.gitlab.arturbosch.detekt.rules.empty"
            ).assert()
        }

        it("exceptionsSection") {
            ConfigAssert(
                config,
                "exceptions",
                "io.gitlab.arturbosch.detekt.rules.exceptions"
            ).assert()
        }

        it("performanceSection") {
            ConfigAssert(
                config,
                "performance",
                "io.gitlab.arturbosch.detekt.rules.performance"
            ).assert()
        }

        it("potentialBugsSection") {
            ConfigAssert(
                config,
                "potential-bugs",
                "io.gitlab.arturbosch.detekt.rules.bugs"
            ).assert()
        }

        it("styleSection") {
            ConfigAssert(
                config,
                "style",
                "io.gitlab.arturbosch.detekt.rules.style"
            ).assert()
        }
    }
})
