package dev.detekt.utils

sealed class YML(open val indent: Int = 0, open var content: String = "") {
    fun append(value: String) {
        content = if (content.isEmpty()) {
            "${getIndent()}$value"
        } else {
            "$content\n${getIndent()}$value"
        }
    }

    fun emptyLine() {
        content = "$content\n"
    }

    private fun getIndent(): String = SINGLE_INDENT.repeat(indent)
}

data class YamlNode(override val indent: Int = 0, override var content: String = "") : YML()

inline fun yaml(content: YamlNode.() -> Unit): String =
    YamlNode().let { yaml ->
        content(yaml)
        yaml.content
    }

fun YamlNode.node(name: String, node: YamlNode.() -> Unit) {
    val yamlNode = YamlNode(indent = indent + 1, content = "$name:")
    node(yamlNode)
    append(yamlNode.content)
}

inline fun YamlNode.keyValue(comment: String = "", keyValue: () -> Pair<String, String>) {
    val (key, value) = keyValue()
    if (comment.isBlank()) {
        append("$key: $value")
    } else {
        append("$key: $value # $comment")
    }
}

fun YamlNode.comment(comment: String = "") {
    append("# $comment")
}

fun YamlNode.list(name: String, list: List<String>) {
    if (list.isEmpty()) {
        keyValue { name to EMPTY_LIST }
    } else {
        append("$name:")
        list.forEach {
            append("${SINGLE_INDENT}${LIST_PREFIX}${it.ensureQuoted()}")
        }
    }
}

fun YamlNode.listOfMaps(name: String, maps: List<Map<String, String?>>) {
    val noneEmptyMaps = maps.filter { it.isNotEmpty() }
    if (noneEmptyMaps.isEmpty()) {
        list(name, emptyList())
    } else {
        node(name) {
            maps.forEach { map(it) }
        }
    }
}

private fun YamlNode.map(map: Map<String, String?>) {
    map.entries
        .filter { it.value != null }
        .sortedBy { it.key }
        .forEachIndexed { index, (key, value) ->
            val prefix = if (index == 0) {
                LIST_PREFIX
            } else {
                SINGLE_INDENT
            }
            keyValue { "$prefix$key" to (value?.ensureQuoted() ?: error("value cannot be null")) }
        }
}

inline fun YamlNode.yaml(yaml: () -> String): Unit = append(yaml())

private fun String.ensureQuoted(): String =
    when {
        isBlank() -> quoted()

        (startsWith(SINGLE_QUOTE) && endsWith(SINGLE_QUOTE)) ||
            (startsWith(DOUBLE_QUOTE) && endsWith(DOUBLE_QUOTE)) -> this

        else -> quoted()
    }

private fun String.quoted() = "'$this'"

private const val SINGLE_INDENT = "  "
private const val SINGLE_QUOTE = "'"
private const val DOUBLE_QUOTE = "\""
private const val EMPTY_LIST = "[]"
private const val LIST_PREFIX = "- "
