package io.gitlab.arturbosch.detekt.generator.collection

data class Configuration(
    val name: String,
    val description: String,
    val defaultValue: String,
    val defaultAndroidValue: String?,
    val deprecated: String?
) {
    fun isDeprecated() = deprecated != null

    fun isDefaultValueNonEmptyList() = defaultValue.isNonEmptyYamlList() || defaultValue.isNonEmptyBracketList()

    fun getDefaultValueAsList(): List<String> {
        return when {
            defaultValue.isNonEmptyYamlList() -> defaultValue.toListFromYamlList()
            defaultValue.isNonEmptyBracketList() -> defaultValue.toListFromBracketList()
            else -> error("default value '$defaultValue' is not a list")
        }
    }

    private fun String.isNonEmptyYamlList() = trim().startsWith("- ")
    private fun String.toListFromYamlList(): List<String> =
        split("\n")
            .map { it.replace("-", "") }
            .map { it.trim() }

    private fun String.isNonEmptyBracketList(): Boolean = NON_EMPTY_BRACKET_LIST_REGEX.matchEntire(this) != null

    private fun String.toListFromBracketList(): List<String> =
        trim()
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { it.trim().removeSurrounding("'") }

    companion object {
        private val NON_EMPTY_BRACKET_LIST_REGEX = Regex("""\[.*[\S]+.*]""")
    }
}
