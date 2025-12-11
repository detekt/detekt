package dev.detekt.cli.runners

import dev.detekt.cli.parseArguments
import dev.detekt.test.NullPrintStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.readLines

class ConfigExporterSpec {

    @Test
    fun `should export the given config`(@TempDir tempDir: Path) {
        val tmpConfig = tempDir.resolve("ConfigPrinterSpec.yml")
        val cliArgs = parseArguments(arrayOf("--generate-config", tmpConfig.toString()))

        ConfigExporter(cliArgs, NullPrintStream()).execute()

        assertThat(tmpConfig.readLines()).isNotEmpty
    }
}
