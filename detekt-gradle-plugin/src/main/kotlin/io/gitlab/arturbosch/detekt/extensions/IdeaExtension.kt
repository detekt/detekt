package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

@Suppress("UnsafeCallOnNullableType")
open class IdeaExtension {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var path: String? = null

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var codeStyleScheme: String? = null

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    var inspectionsProfile: String? = null

    @get:OutputDirectory
    var report: String? = null

    @get:Input
    var mask: String = "*.kt"

    fun formatArgs(input: String): Array<String> {
        requireNotNull(path) { IDEA_PATH_ERROR }
        return if (codeStyleScheme != null) {
            arrayOf(formatScript(path!!), "-r", input, "-s", codeStyleScheme!!, "-m", mask)
        } else {
            arrayOf(formatScript(path!!), "-r", input, "-m", mask)
        }
    }

    fun inspectArgs(input: String): Array<String> {
        requireNotNull(path) { IDEA_PATH_ERROR }
        requireNotNull(report) { REPORT_PATH_ERROR }
        requireNotNull(inspectionsProfile) { INSPECTION_PROFILE_ERROR }
        return arrayOf(inspectScript(path!!), input, inspectionsProfile!!, report!!)
    }

    override fun toString(): String = "IdeaExtension(path=$path, " +
        "codeStyleScheme=$codeStyleScheme, inspectionsProfile=$inspectionsProfile, report=$report, mask='$mask')"
}

private val isWindows: Boolean = System.getProperty("os.name").contains("Windows")

private const val IDEA_PATH_ERROR = "Make sure the idea path is specified to run idea tasks!"
private const val REPORT_PATH_ERROR =
    "Make sure the report path is specified where idea inspections are stored!"
private const val INSPECTION_PROFILE_ERROR =
    "Make sure the path to an inspection profile is provided!"

private fun inspectScript(path: String): String = "$path/bin/" + if (isWindows) "inspect.bat" else "inspect.sh"
private fun formatScript(path: String): String = "$path/bin/" + if (isWindows) "format.bat" else "format.sh"
