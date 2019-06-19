package io.gitlab.arturbosch.detekt.invoke

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import java.net.URLClassLoader

/**
 * @author Marvin Ramin
 * @author Matthew Haughton
 */
@Suppress("TooGenericExceptionCaught", "ThrowsCount", "NestedBlockDepth", "UnusedPrivateMember", "ComplexMethod")
object DetektInvoker {

    private const val MAIN_CLASS_NAME = "io.gitlab.arturbosch.detekt.cli.Main"
    private const val BUILD_FAILURE_CLASS_NAME = "io.gitlab.arturbosch.detekt.cli.console.BuildFailure"
    private const val CONFIG_EXPORTER_CLASS_NAME = "io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter"
    private const val RUNNER_CLASS_NAME = "io.gitlab.arturbosch.detekt.cli.runners.Runner"

    private const val MAIN_PARSE_ARGUMENTS_METHOD_NAME = "parseArguments"
    private const val RUNNER_EXECUTE_METHOD_NAME = "execute"

    internal fun invokeCli(
        project: Project,
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean = false
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)

        project.logger.debug(cliArguments.joinToString(" "))

        try {
            val loader = URLClassLoader(
                classpath.map { it.toURI().toURL() }.toTypedArray(),
                DetektInvoker.javaClass.classLoader
            )
<<<<<<< HEAD

            val mainClass = loader.loadClass(MAIN_CLASS_NAME)
            val parseArguments = checkNotNull(mainClass.declaredMethods
                .find { it.name == MAIN_PARSE_ARGUMENTS_METHOD_NAME })
            parseArguments.isAccessible = true
            val cliArgs = parseArguments.invoke(null, cliArguments.toTypedArray())

            val (runner, runnerClass) = if (arguments.find { it is GenerateConfigArgument } != null) {
                val runnerClass = loader.loadClass(CONFIG_EXPORTER_CLASS_NAME)
                val runner = runnerClass.newInstance()
                runner to runnerClass
            } else {
                val runnerClass = loader.loadClass(RUNNER_CLASS_NAME)
                val runner = runnerClass.declaredConstructors.first().newInstance(cliArgs)
                runner to runnerClass
            }

=======

            val mainClass = loader.loadClass(MAIN_CLASS_NAME)
            val parseArguments = checkNotNull(mainClass.declaredMethods
                .find { it.name == MAIN_PARSE_ARGUMENTS_METHOD_NAME })
            parseArguments.isAccessible = true
            val cliArgs = parseArguments.invoke(null, cliArguments.toTypedArray())

            val (runner, runnerClass) = if (arguments.find { it is GenerateConfigArgument } != null) {
                val runnerClass = loader.loadClass(CONFIG_EXPORTER_CLASS_NAME)
                val runner = runnerClass.newInstance()
                runner to runnerClass
            } else {
                val runnerClass = loader.loadClass(RUNNER_CLASS_NAME)
                val runner = runnerClass.declaredConstructors.first().newInstance(cliArgs)
                runner to runnerClass
            }

>>>>>>> Use reflection to invoke detekt cli in gradle plugin - #1686
            val executeMethod = checkNotNull(runnerClass.declaredMethods.find { it.name == RUNNER_EXECUTE_METHOD_NAME })

            try {
                executeMethod.invoke(runner)
            } catch (e: Exception) {
                if (e.javaClass.name == BUILD_FAILURE_CLASS_NAME ||
                    e.cause?.javaClass?.name == BUILD_FAILURE_CLASS_NAME) {
                    if (!ignoreFailures) {
                        throw GradleException("MaxIssues or failThreshold count was reached.")
                    }
                } else {
                    throw e
                }
            } finally {
                loader.close()
            }
            println("Successfully run detekt via reflection")
        } catch (e: Exception) {
            e.printStackTrace()
            throw GradleException("There was a problem running detekt.")
        }
    }
}
