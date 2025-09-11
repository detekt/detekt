package dev.detekt.cli.runners

import dev.detekt.api.internal.whichDetekt
import dev.detekt.test.utils.StringPrintStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VersionPrinterSpec {

    @Test
    fun `prints the version`() {
        val printStream = StringPrintStream()

        VersionPrinter(printStream).execute()

        assertThat(printStream.toString()).contains(whichDetekt())
    }
}
