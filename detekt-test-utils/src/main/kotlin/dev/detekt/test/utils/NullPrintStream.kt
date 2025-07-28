package dev.detekt.test.utils

import java.io.OutputStream
import java.io.PrintStream

class NullPrintStream : PrintStream(NullOutputStream())

internal class NullOutputStream : OutputStream() {
    override fun write(b: Int) {
        // no-op
    }
}
