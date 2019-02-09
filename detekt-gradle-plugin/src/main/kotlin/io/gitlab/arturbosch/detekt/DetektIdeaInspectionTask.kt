package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import io.gitlab.arturbosch.detekt.invoke.ProcessExecutor
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektIdeaInspectionTask : DefaultTask() {

	init {
		description = "Uses an external idea installation to inspect your code."
		group = LifecycleBasePlugin.VERIFICATION_GROUP
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	var input: ConfigurableFileCollection = project.layout.configurableFiles()

	@Internal
	@Optional
	var debug: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

	@Internal
	lateinit var ideaExtension: IdeaExtension

	@TaskAction
	fun inspect() {
		val debugState = debug.getOrElse(false)
		if (debugState) {
			println("Running inspection task in debug mode")
			println("$ideaExtension")
		}
		ProcessExecutor.startProcess(ideaExtension.inspectArgs(input.asPath), debugState)
	}
}
