package io.gitlab.arturbosch.detekt

/**
 * @author Artur Bosch
 */
open class IdeaExtension(open var path: String? = null,
						 open var codeStyleScheme: String? = null,
						 open var inspectionsProfile: String? = null,
						 open var mask: String = "*.kt") {

	override fun toString(): String {
		return "IdeaExtension(path=$path, codeStyleScheme=$codeStyleScheme, inspectionsProfile=$inspectionsProfile, mask='$mask')"
	}
}