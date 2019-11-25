package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import io.gitlab.arturbosch.detekt.invoke.ProcessExecutor.startProcess
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

@CacheableTask
open class DetektIdeaInspectionTask : SourceTask() {

    init {
        description = "Uses an external idea installation to inspect your code."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @get:Nested
    lateinit var ideaExtension: IdeaExtension

    @TaskAction
    fun inspect() {
        logger.debug("$ideaExtension")
        startProcess(ideaExtension.inspectArgs(source.asPath))
    }
}
