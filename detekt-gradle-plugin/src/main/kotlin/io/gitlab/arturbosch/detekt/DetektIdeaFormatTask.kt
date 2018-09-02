package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import io.gitlab.arturbosch.detekt.invoke.ProcessExecutor.startProcess
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
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
open class DetektIdeaFormatTask : DefaultTask() {

	init {
		description = "Uses an external idea installation to format your code."
		group = LifecycleBasePlugin.VERIFICATION_GROUP
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	lateinit var input: FileCollection

	@Internal
	@Optional
	var debugOrDefault: Boolean = false

	lateinit var ideaExtension: IdeaExtension

	@TaskAction
	fun format() {
		if (debugOrDefault) println("$ideaExtension")

		startProcess(ideaExtension.formatArgs(input.asPath))
	}
}
