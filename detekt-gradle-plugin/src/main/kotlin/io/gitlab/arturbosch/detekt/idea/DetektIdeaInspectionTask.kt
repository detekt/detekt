package io.gitlab.arturbosch.detekt.idea

import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

@CacheableTask
open class DetektIdeaInspectionTask : IdeaCommandLineTask() {

    init {
        description = "Uses an external idea installation to inspect your code."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    private fun inspectArgs(): List<String> {
        val inspectScript = "$ideaDirectory/bin/" + if (isWindows) "inspect.bat" else "inspect.sh"
        requireNotNull(ideaDirectory) { IDEA_PATH_ERROR }
        requireNotNull(report) { REPORT_PATH_ERROR }
        requireNotNull(inspectionsProfile) { INSPECTION_PROFILE_ERROR }
        return listOf(inspectScript, projectDirectory.toString(), inspectionsProfile!!, report!!.toString())
    }

    @TaskAction
    fun inspect() {
        project.exec { it.commandLine(inspectArgs()) }
            .assertNormalExitValue()
            .rethrowFailure()
    }
}
