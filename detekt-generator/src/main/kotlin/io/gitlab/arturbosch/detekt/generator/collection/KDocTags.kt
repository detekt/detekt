package io.gitlab.arturbosch.detekt.generator.collection

import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * @author Marvin Ramin
 * @author Artur Bosch
 */

fun KtClassOrObject.parseConfigurationTags() =
		kDocSection()?.findTagsByName(TAG_CONFIGURATION)
				?.filter { it.isValidConfigurationTag() }
				?.map { it.parseConfigTag() }
				?: emptyList()

fun KtClassOrObject.kDocSection(): KDocSection? = docComment?.getDefaultSection()

fun KDocTag.parseConfigTag(): Configuration {
	val content: String = getContent()
	val delimiterIndex = content.indexOf('-')
	val name = content.substring(0, delimiterIndex - 1)
	val defaultValue = configurationDefaultValueRegex.find(content)
			?.groupValues
			?.get(1)
			?.trim() ?: ""
	val description = content.substring(delimiterIndex + 1)
			.replace(configurationDefaultValueRegex, "")
			.trim()
	return Configuration(name, description, defaultValue)
}

fun KDocTag.isValidConfigurationTag(entity: String = "Rule"): Boolean {
	val content: String = getContent()
	val valid = content.contains("-")
			&& content.contains(configurationDefaultValueRegex)
	if (!valid) {
		throw InvalidDocumentationException(
				"$entity $name contains an incorrect configuration option tag in the KDoc.")
	}
	return valid
}

val configurationDefaultValueRegex = "\\(default: (.+)\\)".toRegex(RegexOption.DOT_MATCHES_ALL)
const val TAG_CONFIGURATION = "configuration"
