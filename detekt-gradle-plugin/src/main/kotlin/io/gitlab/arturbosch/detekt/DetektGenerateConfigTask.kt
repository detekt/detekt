package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.GenerateConfigArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

open class DetektGenerateConfigTask : SourceTask() {

    init {
        description = "Generate a detekt configuration file inside your project."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @Deprecated("Replace with getSource/setSource")
    var input: FileCollection
        get() = source
        set(value) = setSource(value)

    @Classpath
    val detektClasspath = project.configurableFileCollection()

    @TaskAction
    fun generateConfig() {
        val arguments = mutableListOf(
            GenerateConfigArgument,
            InputArgument(source)
        )

        DetektInvoker.create(project).invokeCli(arguments.toList(), detektClasspath, name)
    }
}
