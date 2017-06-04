package io.gitlab.arturbosch.detekt

/**
 * @author Artur Bosch
 */
open class IdeaExtension(open var path: String? = null,
						 open var codeStyleScheme: String? = null,
						 open var inspectionsProfile: String? = null,
						 open var mask: String = "*.kt") {

	fun formatArgs(ext: DetektExtension) = arrayOf("$path/bin/format.sh", "-r", ext.input, "-s", codeStyleScheme, "-m", mask)
	fun inspectArgs(ext: DetektExtension) = arrayOf("$path/bin/inspect.sh", ext.input, inspectionsProfile, ext.report)

	override fun toString(): String {
		return "IdeaExtension(path=$path, codeStyleScheme=$codeStyleScheme, inspectionsProfile=$inspectionsProfile, mask='$mask')"
	}
}