package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Files

class ConfigExporterSpec {

    @Test
    fun `should export the given config`() {
        val tmpConfig = createTempFileForTest("ConfigPrinterSpec", ".yml")
        val cliArgs = parseArguments(arrayOf("--config", tmpConfig.toString()))

        ConfigExporter(cliArgs, NullPrintStream(), NullPrintStream()).execute()

        assertThat(Files.readAllLines(tmpConfig)).isNotEmpty
    }
}
