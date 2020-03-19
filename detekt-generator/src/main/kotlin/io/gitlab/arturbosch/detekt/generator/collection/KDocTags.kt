package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtClassOrObject

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
    val deprecatedMessage = configurationDeprecatedRegex.find(content)
            ?.groupValues
            ?.get(1)
            ?.trim()
    val description = content.substring(delimiterIndex + 1)
            .replace(configurationDefaultValueRegex, "")
            .replace(configurationDeprecatedRegex, "")
            .trim()
    return Configuration(name, description, defaultValue, deprecatedMessage)
}

private const val EXPECTED_CONFIGURATION_FORMAT =
        "Expected format: @configuration {paramName} - {paramDescription} (default: `{defaultValue}`)."

fun KDocTag.isValidConfigurationTag(entity: String = "Rule"): Boolean {
    val content: String = getContent()
    if (!content.contains(" - ")) {
        throw InvalidDocumentationException(
                "[${containingFile.name}] $entity '$content' doesn't seem to contain a description.\n" +
                        EXPECTED_CONFIGURATION_FORMAT)
    }
    if (content.substringAfter("`", "").substringBeforeLast("`", "").isBlank()) {
        val parameterName = content.substringBefore(" - ")
        throw InvalidDocumentationException(
                "[${containingFile.name}] $entity '$parameterName' doesn't seem to contain a default value.\n" +
                        EXPECTED_CONFIGURATION_FORMAT)
    }
    return true
}

val configurationDefaultValueRegex = "\\(default: `(.+)`\\)".toRegex(RegexOption.DOT_MATCHES_ALL)
val configurationDeprecatedRegex = "\\(deprecated: \"(.+)\"\\)".toRegex(RegexOption.DOT_MATCHES_ALL)
const val TAG_CONFIGURATION = "configuration"
