package io.gitlab.arturbosch.detekt.generator.out

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

    private fun getIndent(): String {
        var spaces = ""
        indent times {
            spaces += SINGLE_INDENT
        }
        return spaces
    }
}

data class YamlNode(override val indent: Int = 0, override var content: String = "") : YML()

infix fun Int.times(function: () -> Unit) {
    var i = this
    while (i > 0) {
        function()
        i--
    }
}

inline fun yaml(content: YamlNode.() -> Unit): String {
    return YamlNode().let { yaml ->
        content(yaml)
        yaml.content
    }
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

fun YamlNode.list(name: String, list: List<String>) {
    append("$name:")
    list.forEach {
        append("$SINGLE_INDENT- ${it.quotedForList()}")
    }
}

inline fun YamlNode.yaml(yaml: () -> String) = append(yaml())

private fun String.quotedForList(): String {
    return when {
        isBlank() -> quoted()
        startsWith(SINGLE_QUOTE) && endsWith(SINGLE_QUOTE)
            || startsWith(DOUBLE_QUOTE) && endsWith(DOUBLE_QUOTE) -> this
        matches(NO_QUOTES_REQUIRED) -> this
        else -> quoted()
    }
}

private fun String.quoted() = "'$this'"

private const val SINGLE_INDENT = "  "
private const val SINGLE_QUOTE = "'"
private const val DOUBLE_QUOTE = "\""
private val NO_QUOTES_REQUIRED = Regex("""[.\w\s-]+""")
