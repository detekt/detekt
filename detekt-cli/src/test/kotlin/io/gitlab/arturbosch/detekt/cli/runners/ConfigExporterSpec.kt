package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.gitlab.arturbosch.detekt.cli.parseArguments
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
