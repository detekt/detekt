package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.ExplainedValues
import io.gitlab.arturbosch.detekt.generator.out.YamlNode
import io.gitlab.arturbosch.detekt.generator.out.keyValue
import io.gitlab.arturbosch.detekt.generator.out.list
import io.gitlab.arturbosch.detekt.generator.out.listOfMaps

sealed interface DefaultValue {

    fun printAsYaml(name: String, yaml: YamlNode)

    fun isNonEmptyList(): Boolean = false
    fun getAsList(): List<String> = error("default value is not a list")
    fun getAsPlainString(): String = toString()
    fun getQuotedIfNecessary(): String = getAsPlainString()

    companion object {
        fun of(defaultValue: String): DefaultValue = StringDefault(defaultValue)
        fun of(defaultValue: Boolean): DefaultValue = BooleanDefault(defaultValue)
        fun of(defaultValue: Int): DefaultValue = IntegerDefault(defaultValue)
        fun of(defaultValue: List<String>): DefaultValue = StringListDefault(defaultValue)
        fun of(defaultValue: ExplainedValues): DefaultValue = ExplainedValuesDefault(defaultValue)
    }
}

private data class StringDefault(private val defaultValue: String) : DefaultValue {
    private val quoted = "'$defaultValue'"
    override fun printAsYaml(name: String, yaml: YamlNode) {
        yaml.keyValue { name to quoted }
    }

    override fun getAsPlainString(): String = defaultValue
    override fun getQuotedIfNecessary(): String = quoted
}

private data class BooleanDefault(private val defaultValue: Boolean) : DefaultValue {
    override fun getAsPlainString(): String = defaultValue.toString()
    override fun printAsYaml(name: String, yaml: YamlNode) {
        yaml.keyValue { name to defaultValue.toString() }
    }
}

private data class IntegerDefault(private val defaultValue: Int) : DefaultValue {
    override fun getAsPlainString(): String = defaultValue.toString()
    override fun printAsYaml(name: String, yaml: YamlNode) {
        yaml.keyValue { name to defaultValue.toString() }
    }
}

private data class StringListDefault(private val defaultValue: List<String>) : DefaultValue {
    private val quoted: String = defaultValue.map { "'$it'" }.toString()
    override fun printAsYaml(name: String, yaml: YamlNode) {
        yaml.list(name, defaultValue)
    }

    override fun isNonEmptyList(): Boolean = defaultValue.isNotEmpty()
    override fun getAsList(): List<String> = defaultValue.ifEmpty { error("default value is an empty list") }
    override fun getAsPlainString(): String = defaultValue.toString()
    override fun getQuotedIfNecessary(): String = quoted
}

private data class ExplainedValuesDefault(private val defaultValue: ExplainedValues) : DefaultValue {
    override fun printAsYaml(name: String, yaml: YamlNode) {
        val asMap: List<Map<String, String?>> = defaultValue.values.map { mapOf("value" to it.value, "reason" to it.reason) }
        yaml.listOfMaps(name, asMap)
    }
}
