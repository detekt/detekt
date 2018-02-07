package io.gitlab.arturbosch.detekt.extensions

/**
 * @author Artur Bosch
 */
open class IdeaExtension(open var path: String? = null,
						 open var codeStyleScheme: String? = null,
						 open var inspectionsProfile: String? = null,
						 open var report: String? = null,
						 open var mask: String = "*.kt") {

	fun formatArgs(ext: DetektExtension): Array<String> {
		val input = ext.profileInputPath()
		require(path != null) { IDEA_PATH_ERROR }
		require(input != null) { INPUT_PATH_ERROR }
		return if (codeStyleScheme != null) {
			arrayOf(formatScript(path!!), "-r", input!!, "-s", codeStyleScheme!!, "-m", mask)
		} else {
			arrayOf(formatScript(path!!), "-r", input!!, "-m", mask)
		}
	}

	fun inspectArgs(ext: DetektExtension): Array<String> {
		val input = ext.profileInputPath()
		require(path != null) { IDEA_PATH_ERROR }
		require(input != null) { INPUT_PATH_ERROR }
		require(report != null) { REPORT_PATH_ERROR }
		require(inspectionsProfile != null) { INSPECTION_PROFILE_ERROR }
		return arrayOf(inspectScript(path!!), input!!, inspectionsProfile!!, report!!)
	}

	private fun DetektExtension.profileInputPath() = ProfileStorage.systemOrDefault.input?.apply {
		if (debug) println("input: $this")
	}

	override fun toString(): String = "IdeaExtension(path=$path, " +
			"codeStyleScheme=$codeStyleScheme, inspectionsProfile=$inspectionsProfile, report=$report, mask='$mask')"
}

private val isWindows: Boolean = System.getProperty("os.name").contains("Windows")

private const val INPUT_PATH_ERROR = "Make sure the input path is specified!"
private const val IDEA_PATH_ERROR = "Make sure the idea path is specified to run idea tasks!"
private const val REPORT_PATH_ERROR =
		"Make sure the report path is specified where idea inspections are stored!"
private const val INSPECTION_PROFILE_ERROR =
		"Make sure the path to an inspection profile is provided!"

private fun inspectScript(path: String): String = "$path/bin/" + if (isWindows) "inspect.bat" else "inspect.sh"
private fun formatScript(path: String): String = "$path/bin/" + if (isWindows) "format.bat" else "format.sh"
