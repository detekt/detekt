package io.gitlab.arturbosch.detekt

/**
 * @author Artur Bosch
 */
open class IdeaExtension(open var path: String? = null,
						 open var codeStyleScheme: String? = null,
						 open var inspectionsProfile: String? = null,
						 open var mask: String = "*.kt") {

	fun formatArgs(ext: DetektExtension): Array<String> {
		require(path != null) { "Make sure the idea path is specified to run idea tasks!" }
		require(ext.input != null) { "Make sure the project path is specified!" }
		return if (codeStyleScheme != null) {
			arrayOf("$path/bin/format.sh", "-r", ext.input!!, "-s", codeStyleScheme!!, "-m", mask)
		} else {
			arrayOf("$path/bin/format.sh", "-r", ext.input!!, "-m", mask)
		}
	}

	fun inspectArgs(ext: DetektExtension): Array<String> {
		require(path != null) { "Make sure the idea path is specified to run idea tasks!" }
		require(ext.input != null) { "Make sure the project path is specified!" }
		require(ext.output != null) { "Make sure the output file is specified where idea inspections are stored!" }
		require(inspectionsProfile != null) { "Make sure the path to an inspection profile is provided!" }
		return arrayOf("$path/bin/inspect.sh", ext.input!!, inspectionsProfile!!, ext.output!!)
	}

	override fun toString(): String {
		return "IdeaExtension(path=$path, codeStyleScheme=$codeStyleScheme, inspectionsProfile=$inspectionsProfile, mask='$mask')"
	}
}

