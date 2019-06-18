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

private val isColoredOutputSupported: Boolean = !System.getProperty("os.name", "").contains("win", true)
private fun CharSequence.colorized(color: Color): CharSequence = if (isColoredOutputSupported) {
    "${color.escapeSequence}$this${RESET.escapeSequence}"
} else {
    this
}

fun CharSequence.red() = colorized(RED)
fun CharSequence.yellow() = colorized(YELLOW)

fun CharSequence.decolorized() = this.replace(escapeSequenceRegex, "")
