package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.core.Detektor
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.Charset

class VersionPrinterSpec : Spek({

    describe("version printer") {

        it("print the version") {
            val byteArrayOutputStream = ByteArrayOutputStream()

            VersionPrinter(PrintStream(byteArrayOutputStream)).execute()

            assertThat(String(byteArrayOutputStream.toByteArray(), Charset.forName("UTF-8")))
                .isEqualTo("1.6.0" + System.lineSeparator())
        }
    }
})
