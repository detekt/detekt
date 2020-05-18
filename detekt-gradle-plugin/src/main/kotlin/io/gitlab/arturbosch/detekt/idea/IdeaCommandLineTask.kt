package io.gitlab.arturbosch.detekt.idea

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File

/**
 * https://www.jetbrains.com/help/idea/command-line-code-inspector.html
 */
open class IdeaCommandLineTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var projectDirectory: File = project.rootDir

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var ideaDirectory: File? = null

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var codeStyleScheme: String? = null

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var inspectionsProfile: String? = null

    @get:OutputDirectory
    var report: File? = null

    @get:Input
    var mask: String = "**/src/**/*.kt"

    companion object {
        internal val isWindows: Boolean = System.getProperty("os.name").contains("Windows")
        internal const val IDEA_PATH_ERROR = "Make sure the idea path is specified to run idea tasks!"
        internal const val REPORT_PATH_ERROR =
            "Make sure the report path is specified where idea inspections are stored!"
        internal const val INSPECTION_PROFILE_ERROR = "Make sure the path to an inspection profile is provided!"
    }
}
