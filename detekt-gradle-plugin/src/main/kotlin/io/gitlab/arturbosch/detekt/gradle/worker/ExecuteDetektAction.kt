package io.gitlab.arturbosch.detekt.gradle.worker

import io.github.detekt.tooling.api.DetektCli
import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.UnexpectedError
import org.gradle.api.GradleException
import org.gradle.workers.WorkAction

abstract class ExecuteDetektAction : WorkAction<CliParams> {

    override fun execute() {
        val args = parameters.cliArguments.get().toTypedArray()
        val result = DetektCli.load().run(args)

        val cause = when (val error = result.error) {
            is UnexpectedError -> error.cause
            else -> error
        }

        when {
            cause is MaxIssuesReached && parameters.ignoreFailures.get() -> return
            cause != null -> throw GradleException(cause.message ?: "There was a problem running detekt.", cause)
        }
    }
}
