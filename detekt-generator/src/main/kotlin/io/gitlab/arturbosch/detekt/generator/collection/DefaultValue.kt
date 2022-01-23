package io.gitlab.arturbosch.detekt.generator.collection

sealed interface DefaultValue {
    fun isNonEmptyList(): Boolean = false
    fun getAsList(): List<String> = error("default value is not a list")
    fun getAsPlainString(): String = toString()
    fun getQuotedIfNecessary(): String = getAsPlainString()

    companion object {
        fun of(defaultValue: String): DefaultValue = StringDefault(defaultValue)
        fun of(defaultValue: Boolean): DefaultValue = BooleanDefault(defaultValue)
        fun of(defaultValue: Int): DefaultValue = IntegerDefault(defaultValue)
        fun of(defaultValue: List<String>): DefaultValue = StringListDefault(defaultValue)
    }
}

private data class StringDefault(private val defaultValue: String) : DefaultValue {
    private val quoted = "'$defaultValue'"
    override fun getAsPlainString(): String = defaultValue
    override fun getQuotedIfNecessary(): String = quoted
}

private data class BooleanDefault(private val defaultValue: Boolean) : DefaultValue {
    override fun getAsPlainString(): String = defaultValue.toString()
}

private data class IntegerDefault(private val defaultValue: Int) : DefaultValue {
    override fun getAsPlainString(): String = defaultValue.toString()
}

private data class StringListDefault(private val defaultValue: List<String>) : DefaultValue {
    private val quoted: String = defaultValue.map { "'$it'" }.toString()

    override fun isNonEmptyList(): Boolean = defaultValue.isNotEmpty()
    override fun getAsList(): List<String> = defaultValue.ifEmpty { error("default value is an empty list") }
    override fun getAsPlainString(): String = defaultValue.toString()
    override fun getQuotedIfNecessary(): String = quoted
}
