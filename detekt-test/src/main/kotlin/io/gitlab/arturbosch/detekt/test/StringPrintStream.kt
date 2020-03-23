package io.gitlab.arturbosch.detekt.test

import java.io.ByteArrayOutputStream
import java.io.PrintStream

class StringPrintStream private constructor(
    private val stream: ByteArrayOutputStream
) : PrintStream(stream) {

    constructor() : this(ByteArrayOutputStream())

    override fun toString(): String {
        return stream.toString()
    }
}
