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
		val input = ext.systemOrDefaultProfile?.input
		if (ext.debug) println("input: $input")
		require(path != null) { "Make sure the idea path is specified to run idea tasks!" }
		require(input != null) { "Make sure the project path is specified!" }
		return if (codeStyleScheme != null) {
			arrayOf("$path/bin/format.sh", "-r", input!!, "-s", codeStyleScheme!!, "-m", mask)
		} else {
			arrayOf("$path/bin/format.sh", "-r", input!!, "-m", mask)
		}
	}

	fun inspectArgs(ext: DetektExtension): Array<String> {
		val input = ext.systemOrDefaultProfile?.input
		if (ext.debug) println("input: $input")
		require(path != null) { "Make sure the idea path is specified to run idea tasks!" }
		require(input != null) { "Make sure the project path is specified!" }
		require(report != null) { "Make sure the report path is specified where idea inspections are stored!" }
		require(inspectionsProfile != null) { "Make sure the path to an inspection profile is provided!" }
		return arrayOf("$path/bin/inspect.sh", input!!, inspectionsProfile!!, report!!)
	}

	override fun toString(): String = this.reflectiveToString()

}

