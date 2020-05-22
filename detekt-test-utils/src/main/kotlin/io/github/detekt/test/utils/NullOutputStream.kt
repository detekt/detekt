package io.github.detekt.test.utils

import java.io.OutputStream

internal class NullOutputStream : OutputStream() {
    override fun write(b: Int) {
        // no-op
    }
}
