package io.github.detekt.compiler.plugin.internal

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageUtil

fun MessageCollector.info(msg: String) {
    this.report(CompilerMessageSeverity.INFO, msg)
}

fun MessageCollector.warn(msg: String, location: CompilerMessageSourceLocation? = null) {
    this.report(CompilerMessageSeverity.WARNING, msg, location)
}

fun MessageCollector.error(msg: String) {
    this.report(CompilerMessageSeverity.ERROR, msg)
}

fun MessageCollector.reportFindings(result: Detektion) {
    for (finding in result.findings.values.flatten()) {
        val (message, location) = finding.renderAsCompilerWarningMessage()
        warn(message, location)
    }
}

fun Finding2.renderAsCompilerWarningMessage(): Pair<String, CompilerMessageLocation?> {
    val location = MessageUtil.psiElementToMessageLocation(entity.ktElement)

    val sourceLocation = location?.let {
        CompilerMessageLocation.create(
            location.path,
            entity.location.source.line,
            entity.location.source.column,
            location.lineContent
        )
    }

    return "${rule.id}: $message" to sourceLocation
}
