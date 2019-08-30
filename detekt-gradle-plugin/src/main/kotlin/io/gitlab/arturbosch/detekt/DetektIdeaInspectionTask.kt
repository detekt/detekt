package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import io.gitlab.arturbosch.detekt.invoke.ProcessExecutor.startProcess
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

open class DetektIdeaInspectionTask : SourceTask() {

    init {
        description = "Uses an external idea installation to inspect your code."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @Deprecated("Replace with getSource/setSource")
    var input: FileCollection
        @Internal
        get() = source
        set(value) = setSource(value)

    @Console
    var debug: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @Internal
    lateinit var ideaExtension: IdeaExtension

    @TaskAction
    fun inspect() {
        logger.debug("$ideaExtension")
        startProcess(ideaExtension.inspectArgs(source.asPath), logger)
    }
}
