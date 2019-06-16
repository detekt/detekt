package io.gitlab.arturbosch.detekt.cli.console

import org.fusesource.jansi.Ansi.Color
import org.fusesource.jansi.Ansi.Color.*
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.AnsiPrintStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.Charset

fun CharSequence.red() = Colorizer.red(this)
fun CharSequence.yellow() = Colorizer.yellow(this)

fun CharSequence.decolorized() = Colorizer.decolorized(this)

private object Colorizer {
    init {
        AnsiConsole.systemInstall()
    }

    private fun CharSequence.colorized(color: Color): String {
        return ansi().fg(color).a(this).fg(DEFAULT).reset().toString()
    }

    fun red(csq: CharSequence) = csq.colorized(RED)
    fun yellow(csq: CharSequence) = csq.colorized(YELLOW)

    fun decolorized(csq: CharSequence): String {
        ByteArrayOutputStream().use { outputStream ->
            val charset = Charset.defaultCharset()
            PrintStream(outputStream, false, charset.name()).use { printStream ->
                AnsiPrintStream(printStream).use { filterStream ->
                    filterStream.print(csq)
                }
            }
            return String(outputStream.toByteArray(), charset)
        }
    }
}
