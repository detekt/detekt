package io.gitlab.arturbosch.detekt.core.config.validation

import io.github.detekt.utils.openSafeStream
import java.util.Properties

internal sealed class Deprecation
internal data class DeprecatedRule(
    val ruleSetId: String,
    val ruleId: String,
    val description: String
) : Deprecation()

internal data class DeprecatedProperty(
    val ruleSetId: String,
    val ruleId: String,
    val propertyName: String,
    val description: String
) : Deprecation()

internal fun loadDeprecations(): Set<Deprecation> {
    return ValidationSettings::class.java.classLoader
        .getResource("deprecation.properties")!!
        .openSafeStream()
        .use { inputStream ->
            Properties()
                .apply { load(inputStream) }
                .toDeprecations()
        }
}

private fun Properties.toDeprecations(): Set<Deprecation> {
    return entries
        .map { deprecationFromPath(it.key as String, it.value as String) }
        .toSet()
}

private fun deprecationFromPath(path: String, description: String): Deprecation {
    val pathElements = path.split(">")
    return when (pathElements.size) {
        RULE_PATH_SEGMENTS -> DeprecatedRule(
            ruleSetId = pathElements[0],
            ruleId = pathElements[1],
            description = description
        )

        PROPERTY_PATH_SEGMENTS -> DeprecatedProperty(
            ruleSetId = pathElements[0],
            ruleId = pathElements[1],
            propertyName = pathElements[2],
            description = description
        )

        else ->
            error("Invalid deprecation file. Element '$path' is neither a rule or a rule property.")
    }
}

private const val RULE_PATH_SEGMENTS = 2
private const val PROPERTY_PATH_SEGMENTS = 3
