package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.core.EditorConfig
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering

/**
 * Creates new EditorConfig by merging existing EditorConfig with properties values passed by parameters.
 * Values of properties passed by parameters are more important than properties in sourceEditorConfig.
 */
@Suppress("LongParameterList")
fun EditorConfig.Companion.merge(
    sourceEditorConfig: EditorConfig?,
    indentSize: Int? = null,
    continuationIndentSize: Int? = null,
    maxLineLength: Int? = null,
    insertFinalNewline: Boolean? = null,
    importLayout: String = ImportOrdering.IDEA
): EditorConfig = fromMap(
    HashMap<String, String>().also {
        copyProperty(it, "indent_size", indentSize, sourceEditorConfig)
        copyProperty(it, "continuation_indent_size", continuationIndentSize, sourceEditorConfig)
        copyProperty(it, "max_line_length", maxLineLength, sourceEditorConfig)
        copyProperty(it, "insert_final_newline", insertFinalNewline, sourceEditorConfig)
        copyProperty(it, ImportOrdering.EDITOR_CONFIG_KEY, importLayout, sourceEditorConfig)
    }
)

private fun copyProperty(
    map: MutableMap<String, String>,
    property: String,
    value: Any?,
    sourceEditorConfig: EditorConfig?
) {
    val newValue: String? = value?.toString() ?: sourceEditorConfig?.get(property)
    newValue?.let { map[property] = it }
}
