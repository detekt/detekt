package io.github.detekt.compiler.plugin.internal

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.suppressed
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

fun MessageCollector.reportIssues(result: Detektion) {
    result.issues
        .filterNot { it.suppressed }
        .sortedBy { it.location }
        .forEach { issue ->
            val (message, location) = issue.renderAsCompilerWarningMessage()
            warn(message, location)
        }
}

fun Issue.renderAsCompilerWarningMessage(): Pair<String, CompilerMessageLocation?> {
    val location = MessageUtil.psiElementToMessageLocation(entity.ktElement)

    val sourceLocation = location?.let {
        CompilerMessageLocation.create(
            entity.location.path.toString(),
            entity.location.source.line,
            entity.location.source.column,
            location.lineContent
        )
    }

    return "${ruleInstance.id}: $message" to sourceLocation
}
