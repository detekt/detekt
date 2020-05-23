package io.github.detekt.test.utils

import java.io.PrintStream

class NullPrintStream : PrintStream(NullOutputStream())
