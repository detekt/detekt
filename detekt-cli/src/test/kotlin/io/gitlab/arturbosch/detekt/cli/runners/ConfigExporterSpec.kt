package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.createCliArgs
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files

class ConfigExporterSpec : Spek({

    describe("config exporter") {

        it("should export the given config") {
            val tmpConfig = Files.createTempFile("ConfigPrinterSpec", ".yml")
            val cliArgs = createCliArgs(
                "--config", tmpConfig.toString()
            )

            ConfigExporter(cliArgs).execute()

            assertThat(Files.readAllLines(tmpConfig)).isNotEmpty
        }
    }
})
