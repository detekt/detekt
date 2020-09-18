package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.internal.ClassLoaderCache
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import java.io.PrintStream
import java.lang.reflect.InvocationTargetException

internal interface DetektInvoker {

    fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean = false
    )

    companion object {
        private const val DRY_RUN_PROPERTY = "detekt-dry-run"

        fun create(project: Project): DetektInvoker =
            if (project.isDryRunEnabled()) {
                DryRunInvoker(project.logger)
            } else {
                DefaultCliInvoker()
            }

        private fun Project.isDryRunEnabled(): Boolean {
            return hasProperty(DRY_RUN_PROPERTY) && property(DRY_RUN_PROPERTY) == "true"
        }
    }
}

private class DefaultCliInvoker : DetektInvoker {

    override fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)
        try {
            val loader = ClassLoaderCache.getOrCreate(classpath)
            val clazz = loader.loadClass("io.gitlab.arturbosch.detekt.cli.Main")
            val runner = clazz.getMethod("buildRunner",
                Array<String>::class.java,
                PrintStream::class.java,
                PrintStream::class.java
            ).invoke(null, cliArguments.toTypedArray(), System.out, System.err)
            runner::class.java.getMethod("execute").invoke(runner)
        } catch (reflectionWrapper: InvocationTargetException) {
            val cause = reflectionWrapper.targetException
            val message = cause.message
            if (message != null && isBuildFailure(message) && ignoreFailures) {
                return
            }
            throw GradleException(message ?: "There was a problem running detekt.", cause)
        }
    }

    private fun isBuildFailure(msg: String?) =
        msg != null && "Build failed with" in msg && "issues" in msg
}

private class DryRunInvoker(private val logger: Logger) : DetektInvoker {

    override fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)
        logger.info("Invoking detekt with dry-run.")
        logger.info("Task: $taskName")
        logger.info("Arguments: ${cliArguments.joinToString(" ")}")
        logger.info("Classpath: ${classpath.files}")
        logger.info("Ignore failures: $ignoreFailures")
    }
}
