package dev.detekt.gradle.invoke

import dev.detekt.gradle.internal.ClassLoaderCache
import dev.detekt.gradle.internal.GlobalClassLoaderCache
import dev.detekt.gradle.plugin.internal.verificationExceptionCompat
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.File
import java.io.PrintStream
import java.lang.reflect.InvocationTargetException

internal interface DetektInvoker {

    fun invokeCli(arguments: List<String>, classpath: Set<File>, taskName: String, ignoreFailures: Boolean = false)

    companion object {

        fun create(isDryRun: Boolean = false): DetektInvoker =
            if (isDryRun) {
                DryRunInvoker()
            } else {
                DefaultCliInvoker()
            }
    }
}

internal interface DetektWorkParameters : WorkParameters {
    val arguments: ListProperty<String>
    val ignoreFailures: Property<Boolean>
    val dryRun: Property<Boolean>
    val taskName: Property<String>
    val classpath: ConfigurableFileCollection
}

internal abstract class DetektWorkAction : WorkAction<DetektWorkParameters> {
    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    override fun execute() {
        if (parameters.dryRun.getOrElse(false)) {
            DryRunInvoker().invokeCli(
                parameters.arguments.get(),
                parameters.classpath.files,
                parameters.taskName.get(),
                parameters.ignoreFailures.get()
            )
            return
        }

        DefaultCliInvoker().invokeCli(
            parameters.arguments.get(),
            parameters.classpath.files,
            parameters.taskName.get(),
            parameters.ignoreFailures.get()
        )
    }
}

internal class DefaultCliInvoker(private val classLoaderCache: ClassLoaderCache = GlobalClassLoaderCache) :
    DetektInvoker {

    override fun invokeCli(arguments: List<String>, classpath: Set<File>, taskName: String, ignoreFailures: Boolean) {
        try {
            val loader = classLoaderCache.getOrCreate(classpath)
            val clazz = loader.loadClass("dev.detekt.cli.Main")
            val runner = clazz.getMethod(
                "buildRunner",
                Array<String>::class.java,
                PrintStream::class.java,
                PrintStream::class.java
            ).invoke(null, arguments.toTypedArray(), System.out, System.err)
            runner::class.java.getMethod("execute").invoke(runner)
        } catch (reflectionWrapper: InvocationTargetException) {
            processResult(reflectionWrapper.targetException.message, reflectionWrapper, ignoreFailures)
        }
    }
}

private fun isAnalysisFailure(msg: String) = "Analysis failed with" in msg && "issues" in msg

@Suppress("ThrowsCount")
private fun processResult(message: String?, reflectionWrapper: Exception, ignoreFailures: Boolean) {
    if (message != null && isAnalysisFailure(message)) {
        if (!ignoreFailures) throw verificationExceptionCompat(message, reflectionWrapper)
    } else {
        throw GradleException(message ?: "There was a problem running detekt.", reflectionWrapper)
    }
}

private class DryRunInvoker : DetektInvoker {

    override fun invokeCli(arguments: List<String>, classpath: Set<File>, taskName: String, ignoreFailures: Boolean) {
        println("Invoking detekt with dry-run.")
        println("Task: $taskName")
        println("Arguments: ${arguments.joinToString(" ")}")
        println("Classpath: $classpath")
        println("Ignore failures: $ignoreFailures")
    }
}
