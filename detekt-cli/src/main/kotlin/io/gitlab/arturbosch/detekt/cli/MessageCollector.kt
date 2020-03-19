package io.gitlab.arturbosch.detekt.cli

interface MessageCollector {
    operator fun plusAssign(msg: String)
}
