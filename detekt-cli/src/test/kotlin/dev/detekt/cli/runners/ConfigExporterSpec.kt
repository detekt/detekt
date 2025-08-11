package dev.detekt.cli.runners

import dev.detekt.cli.parseArguments
import dev.detekt.test.utils.NullPrintStream
import dev.detekt.test.utils.createTempFileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.deleteExisting
import kotlin.io.path.readLines

class ConfigExporterSpec {

    @Test
    fun `should export the given config`() {
        val tmpConfig = createTempFileForTest("ConfigPrinterSpec", ".yml").also { it.deleteExisting() }
        val cliArgs = parseArguments(arrayOf("--generate-config", tmpConfig.toString()))

        ConfigExporter(cliArgs, NullPrintStream()).execute()

        assertThat(tmpConfig.readLines()).isNotEmpty
    }
}
