package io.gitlab.arturbosch.detekt.test

import java.io.PrintStream

class NullPrintStream : PrintStream(NullOutputStream())
