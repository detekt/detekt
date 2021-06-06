package io.gitlab.arturbosch.detekt.generator.collection

data class Configuration(
    val name: String,
    val description: String,
    val defaultValue: String,
    val defaultAndroidValue: String?,
    val deprecated: String?
) {
    fun isDeprecated() = deprecated != null

    fun isDefaultValueNonEmptyList() = defaultValue.isNonEmptyList()

    fun getDefaultValueAsList(): List<String> {
        if (defaultValue.isNonEmptyList()) {
            return defaultValue.toList()
        }
        error("default value '$defaultValue' is not a list")
    }

    private fun String.isNonEmptyList(): Boolean = NON_EMPTY_LIST_REGEX.matchEntire(this) != null

    private fun String.toList(): List<String> =
        trim()
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { it.trim().removeSurrounding("'") }

    companion object {
        private val NON_EMPTY_LIST_REGEX = Regex("""\[.*[\S]+.*]""")
    }
}
