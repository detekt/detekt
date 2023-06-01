package io.github.detekt.compiler.plugin.internal

internal class AppendableAdapter(val logging: (String) -> Unit) : Appendable {

    override fun append(csq: CharSequence): Appendable = also { logging(csq.toString()) }

    override fun append(csq: CharSequence, start: Int, end: Int): Appendable =
        also { logging(csq.substring(start, end)) }

    override fun append(c: Char): Appendable = also { logging(c.toString()) }
}
