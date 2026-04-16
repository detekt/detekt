package dev.detekt.core.config.validation

import dev.detekt.api.Notification
import dev.detekt.api.Notification.Level
import dev.detekt.core.config.YamlConfig
import dev.detekt.core.extractRuleName

internal class InvalidPropertiesConfigValidator(
    private val baseline: YamlConfig,
    deprecatedProperties: Set<DeprecatedProperty>,
    private val excludePatterns: Set<Regex>,
) : AbstractYamlConfigValidator() {

    private val deprecatedPropertyPaths: Set<String> = deprecatedProperties
        .map { "${it.ruleSetId}>${it.ruleName}>${it.propertyName}" }
        .toSet()

    override val id: String = "InvalidPropertiesConfigValidator"

    override fun validate(configToValidate: YamlConfig, settings: ValidationSettings): Collection<Notification> =
        testKeys(configToValidate.properties, baseline.properties)

    private fun testKeys(
        configToValidate: Map<String, Any>,
        baseline: Map<String, Any>,
        parentPath: String? = null,
    ): List<Notification> {
        val notifications = mutableListOf<Notification>()
        for (prop in configToValidate.keys) {
            val propertyPath = "${if (parentPath == null) "" else "$parentPath>"}$prop"
            val isExcluded = excludePatterns.any { it.matches(propertyPath) }
            val isDeprecated = deprecatedPropertyPaths.contains(propertyPath)
            if (isExcluded || isDeprecated) {
                continue
            }
            notifications.addAll(
                checkProp(
                    propertyName = prop,
                    propertyPath = propertyPath,
                    configToValidate = configToValidate,
                    baseline = baseline
                )
            )
        }
        return notifications
    }

    @Suppress("UNCHECKED_CAST", "ReturnCount")
    private fun checkProp(
        propertyName: String,
        propertyPath: String,
        configToValidate: Map<String, Any>,
        baseline: Map<String, Any>,
    ): List<Notification> {
        if (!baseline.contains(propertyName)) {
            val ruleName = extractRuleName(propertyName)
            if (ruleName == null || !baseline.contains(ruleName.value)) {
                return listOf(propertyDoesNotExists(propertyPath, propertyName, baseline.keys))
            }
        }
        if (configToValidate[propertyName] is String && baseline[propertyName] is List<*>) {
            return listOf(propertyShouldBeAnArray(propertyPath))
        }

        val next = configToValidate[propertyName] as? Map<String, Any>
        val nextBase = baseline[propertyName] as? Map<String, Any>
        return when {
            next == null && nextBase != null ->
                listOf(nestedConfigurationExpected(propertyPath))

            baseline.contains(propertyName) && next != null && nextBase == null ->
                listOf(unexpectedNestedConfiguration(propertyPath))

            next != null && nextBase != null ->
                testKeys(next, nextBase, propertyPath)

            else -> emptyList()
        }
    }

    companion object {

        internal fun propertyDoesNotExists(
            prop: String,
            misspelledName: String,
            allowedProperties: Set<String>,
        ): Notification {
            val message = buildString {
                append("Property '$prop' is misspelled or does not exist.")
                val closestMatch = findClosestMatch(misspelledName, allowedProperties)
                if (closestMatch != null) {
                    append(" Did you mean '$closestMatch'?")
                }
                append(" Allowed properties: ${allowedProperties.sorted()}.")
                append(" This error may also indicate a detekt plugin is necessary to handle the '$prop' key.")
            }
            return Notification(message, Level.Error)
        }

        internal fun findClosestMatch(name: String, candidates: Set<String>): String? {
            val threshold = maxOf(1, name.length / 3)
            return candidates
                .map { it to levenshteinDistance(name.lowercase(), it.lowercase()) }
                .filter { (_, distance) -> distance <= threshold }
                .minByOrNull { (_, distance) -> distance }
                ?.first
        }

        internal fun nestedConfigurationExpected(prop: String): Notification =
            Notification("Nested config expected for '$prop'.", Level.Error)

        internal fun unexpectedNestedConfiguration(prop: String): Notification =
            Notification("Unexpected nested config for '$prop'.", Level.Error)

        internal fun propertyShouldBeAnArray(prop: String): Notification =
            Notification("Property '$prop' should be a YAML array instead of a String.", Level.Error)
    }
}

private fun levenshteinDistance(a: String, b: String): Int {
    val dp = Array(a.length + 1) { IntArray(b.length + 1) }
    for (i in 0..a.length) dp[i][0] = i
    for (j in 0..b.length) dp[0][j] = j
    for (i in 1..a.length) {
        for (j in 1..b.length) {
            val cost = if (a[i - 1] == b[j - 1]) 0 else 1
            dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
        }
    }
    return dp[a.length][b.length]
}
