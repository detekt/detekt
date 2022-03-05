package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.ExplainedValues
import io.gitlab.arturbosch.detekt.generator.out.YamlNode
import io.gitlab.arturbosch.detekt.generator.out.keyValue
import io.gitlab.arturbosch.detekt.generator.out.list
import io.gitlab.arturbosch.detekt.generator.out.listOfMaps

sealed interface DefaultValue {

    fun getPlainValue(): String
    fun printAsYaml(name: String, yaml: YamlNode)
    fun printAsMarkdownCode(): String

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

    override fun printAsMarkdownCode(): String = quoted

    override fun getPlainValue(): String = defaultValue
}

private data class BooleanDefault(private val defaultValue: Boolean) : DefaultValue {
    override fun getPlainValue(): String = defaultValue.toString()
    override fun printAsYaml(name: String, yaml: YamlNode) {
        yaml.keyValue { name to defaultValue.toString() }
    }

    override fun printAsMarkdownCode(): String = defaultValue.toString()
}

private data class IntegerDefault(private val defaultValue: Int) : DefaultValue {
    override fun getPlainValue(): String = defaultValue.toString()
    override fun printAsYaml(name: String, yaml: YamlNode) {
        yaml.keyValue { name to defaultValue.toString() }
    }

    override fun printAsMarkdownCode(): String = defaultValue.toString()
}

private data class StringListDefault(private val defaultValue: List<String>) : DefaultValue {
    private val quoted: String = defaultValue.map { "'$it'" }.toString()
    override fun printAsYaml(name: String, yaml: YamlNode) {
        yaml.list(name, defaultValue)
    }

    override fun printAsMarkdownCode(): String = quoted
    override fun getPlainValue(): String {
        error("there is no plain string representation for list defaults")
    }
}

private data class ExplainedValuesDefault(private val defaultValue: ExplainedValues) : DefaultValue {
    override fun getPlainValue(): String {
        error("there is no plain string representation for explained value defaults")
    }

    override fun printAsYaml(name: String, yaml: YamlNode) {
        val asMap: List<Map<String, String?>> =
            defaultValue.values.map { mapOf("value" to it.value, "reason" to it.reason) }
        yaml.listOfMaps(name, asMap)
    }

    override fun printAsMarkdownCode(): String {
        return defaultValue.values.map { "'${it.value}'" }.toString()
    }
}
