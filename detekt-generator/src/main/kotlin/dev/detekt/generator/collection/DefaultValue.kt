package dev.detekt.generator.collection

import dev.detekt.utils.YamlNode
import dev.detekt.utils.keyValue
import dev.detekt.utils.list
import dev.detekt.utils.listOfMaps
import dev.detekt.api.ValuesWithReason

sealed interface DefaultValue {

    fun getPlainValue(): String
    fun printAsYaml(name: String, yaml: YamlNode)
    fun printAsMarkdownCode(): String

    companion object {
        fun of(defaultValue: String): DefaultValue = StringDefault(defaultValue)
        fun of(defaultValue: Boolean): DefaultValue = BooleanDefault(defaultValue)
        fun of(defaultValue: Int): DefaultValue = IntegerDefault(defaultValue)
        fun of(defaultValue: List<String>): DefaultValue = StringListDefault(defaultValue)
        fun of(defaultValue: ValuesWithReason): DefaultValue = ValuesWithReasonDefault(defaultValue)
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

private data class ValuesWithReasonDefault(private val defaultValue: ValuesWithReason) : DefaultValue {
    override fun getPlainValue(): String {
        error("there is no plain string representation for values with reason defaults")
    }

    override fun printAsYaml(name: String, yaml: YamlNode) {
        val asMap: List<Map<String, String?>> =
            defaultValue.map { mapOf("value" to it.value, "reason" to it.reason) }
        yaml.listOfMaps(name, asMap)
    }

    override fun printAsMarkdownCode(): String = defaultValue.map { "'${it.value}'" }.toString()
}
