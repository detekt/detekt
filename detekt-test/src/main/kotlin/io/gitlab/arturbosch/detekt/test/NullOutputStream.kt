package io.gitlab.arturbosch.detekt.test

import java.io.OutputStream

internal class NullOutputStream : OutputStream() {
    override fun write(b: Int) {
        // no-op
    }
}
