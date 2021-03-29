package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.ParameterException
import io.github.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

internal class ArgumentConvertersSpec : Spek({
    describe("Support for URLs") {

        it("config resource can be HTTP") {
            val converter = ClasspathResourceConverter()
            val u = converter.convert("http://localhost/detekt-config.yml")
            assertThat(u).isEqualTo(URL("http://localhost/detekt-config.yml"))
        }

        it("config resource can be HTTPS") {
            val converter = ClasspathResourceConverter()
            val u = converter.convert("https://github.com/detekt/detekt/blob/main/detekt-core/src/main/resources/default-detekt-config.yml")
            assertThat(u).isEqualTo(URL("https://github.com/detekt/detekt/blob/main/detekt-core/src/main/resources/default-detekt-config.yml"))
        }

        it("config resource cannot be FTP") {
            val converter = ClasspathResourceConverter()
            assertThatExceptionOfType(ParameterException::class.java)
                .isThrownBy {
                    converter.convert("ftp://localhost")
                }
        }
    }
})
