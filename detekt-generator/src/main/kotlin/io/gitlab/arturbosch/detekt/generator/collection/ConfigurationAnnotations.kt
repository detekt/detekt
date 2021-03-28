package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import org.jetbrains.kotlin.psi.KtParameter
import io.gitlab.arturbosch.detekt.api.internal.Configuration as ConfigAnnotation

fun KtParameter.parseConfigurationAnnotation(): Configuration? {
    return if (isAnnotatedWith(ConfigAnnotation::class)) toConfiguration() else null
}

private fun KtParameter.toConfiguration(): Configuration {
    val parameterName: String = checkNotNull(name)
    val deprecationMessage = firstAnnotationParameterOrNull(Deprecated::class)

    val descriptionWithDefault: String = firstAnnotationParameter(ConfigAnnotation::class)
    val matchResult = DESC_REGEX.matchEntire(descriptionWithDefault)
        ?: throw InvalidDocumentationException(
            "[${containingFile.name}] '$parameterName' doesn't seem to contain a default value.\n" +
                EXPECTED_CONFIGURATION_FORMAT
        )

    val description = checkNotNull(matchResult.groups["description"]?.value)
    val defaultValue = checkNotNull(matchResult.groups["defaultValue"]?.value)

    return Configuration(
        name = parameterName,
        description = description,
        defaultValue = defaultValue,
        deprecated = deprecationMessage
    )
}

private val DESC_REGEX = """^\s*(?<description>.*)\s+\(default:\s*`(?<defaultValue>.+)`\)\s*$"""
    .toRegex(RegexOption.DOT_MATCHES_ALL)

private const val EXPECTED_CONFIGURATION_FORMAT =
    """Expected format: @Configuration("{paramDescription} (default: `{defaultValue}`)")"""
