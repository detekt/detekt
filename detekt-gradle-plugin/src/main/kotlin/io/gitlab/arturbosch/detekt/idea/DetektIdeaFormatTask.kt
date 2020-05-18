package io.gitlab.arturbosch.detekt.idea

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

@CacheableTask
open class DetektIdeaFormatTask : IdeaCommandLineTask() {

    init {
        description = "Uses an external idea installation to format your code."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    private fun formatArgs(): List<String> {
        val formatScript = "$ideaDirectory/bin/" + if (isWindows) "format.bat" else "format.sh"
        requireNotNull(ideaDirectory) { IDEA_PATH_ERROR }
        val args = mutableListOf(formatScript, "-r", projectDirectory.toString(), "-m", mask)
        if (codeStyleScheme != null) {
            args += listOf("-s", codeStyleScheme!!)
        }
        return args
    }

    @TaskAction
    fun format() {
        project.exec { it.commandLine(formatArgs()) }
            .assertNormalExitValue()
            .rethrowFailure()
    }
}
