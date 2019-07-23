package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.YamlConfig
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class DetektYmlConfigTest : Spek({

    describe("detekt YAML config") {

        val config = loadConfig()

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

internal const val CONFIG_FILE = "default-detekt-config.yml"

private fun loadConfig(): Config {
    var workingDirectory = Paths.get(".").toAbsolutePath().normalize()
    if (!workingDirectory.toString().endsWith("detekt-cli")) {
        workingDirectory = workingDirectory.resolve("detekt-cli")
    }
    val defaultConfigPart = Paths.get("src/main/resources/$CONFIG_FILE")
    val file = workingDirectory.resolve(defaultConfigPart)
    return YamlConfig.loadResource(file.toUri().toURL())
}
