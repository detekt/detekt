package io.gitlab.arturbosch.detekt.cli.runners

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.createTempFileForTest
import io.gitlab.arturbosch.detekt.cli.parseArguments
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.io.path.readLines

class ConfigExporterSpec {

    @Test
    fun `should export the given config`() {
        val tmpConfig = createTempFileForTest("ConfigPrinterSpec", ".yml").also { Files.delete(it) }
        val cliArgs = parseArguments(arrayOf("--config", tmpConfig.toString()))

        ConfigExporter(cliArgs, NullPrintStream()).execute()

        assertThat(tmpConfig.readLines()).isNotEmpty
    }
}
