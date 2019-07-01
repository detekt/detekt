package io.gitlab.arturbosch.detekt.cli.console

private const val ESC = "\u001B"
private val RESET = Color(0)
private val RED = Color(31)
private val YELLOW = Color(33)

private val escapeSequenceRegex = """$ESC\[\d+m""".toRegex()

private data class Color(private val value: Byte) {
    val escapeSequence: String
        get() = "$ESC[${value}m"
}

private val isColoredOutputSupported: Boolean = !System.getProperty("os.name", "").startsWith("Windows")
private fun String.colorized(color: Color) = if (isColoredOutputSupported) {
    "${color.escapeSequence}$this${RESET.escapeSequence}"
} else {
    this
}

fun String.red() = colorized(RED).toString()
fun String.yellow() = colorized(YELLOW).toString()
fun String.decolorized() = this.replace(escapeSequenceRegex, "")
