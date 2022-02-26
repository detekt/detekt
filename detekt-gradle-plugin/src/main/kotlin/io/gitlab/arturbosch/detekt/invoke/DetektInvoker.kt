package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.internal.ClassLoaderCache
import io.gitlab.arturbosch.detekt.internal.GlobalClassLoaderCache
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
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

        fun create(task: Task, isDryRun: Boolean): DetektInvoker =
            if (isDryRun) {
                DryRunInvoker(task.logger)
            } else {
                DefaultCliInvoker()
            }
    }
}

private const val DRY_RUN_PROPERTY = "detekt-dry-run"

internal fun Project.isDryRunEnabled(): Boolean {
    return hasProperty(DRY_RUN_PROPERTY) && property(DRY_RUN_PROPERTY) == "true"
}

internal class DefaultCliInvoker(
    private val classLoaderCache: ClassLoaderCache = GlobalClassLoaderCache
) : DetektInvoker {

    override fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)
        try {
            val loader = classLoaderCache.getOrCreate(classpath)
            val clazz = loader.loadClass("io.gitlab.arturbosch.detekt.cli.Main")
            val runner = clazz.getMethod(
                "buildRunner",
                Array<String>::class.java,
                PrintStream::class.java,
                PrintStream::class.java
            ).invoke(null, cliArguments.toTypedArray(), System.out, System.err)
            runner::class.java.getMethod("execute").invoke(runner)
        } catch (reflectionWrapper: InvocationTargetException) {
            val message = reflectionWrapper.targetException.message
            if (message != null && isAnalysisFailure(message) && ignoreFailures) {
                return
            }
            throw GradleException(message ?: "There was a problem running detekt.", reflectionWrapper)
        }
    }

    private fun isAnalysisFailure(msg: String) = "Analysis failed with" in msg && "issues" in msg
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
