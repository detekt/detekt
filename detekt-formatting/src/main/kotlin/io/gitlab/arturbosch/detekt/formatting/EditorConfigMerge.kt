package io.gitlab.arturbosch.detekt.formatting

import com.github.shyiko.ktlint.core.EditorConfig

/**
 * Creates new EditorConfig by merging existing EditorConfig with properties values passed by parameters.
 * Values of properties passed by parameters are more important than properties in sourceEditorConfig.
 *
 * @author Lukasz Jazgar
 */
fun EditorConfig.Companion.merge(
    sourceEditorConfig: EditorConfig?,
    indentSize: Int? = null,
    continuationIndentSize: Int? = null,
    maxLineLength: Int? = null,
    insertFinalNewline: Boolean? = null
): EditorConfig =
        EditorConfig.fromMap(
                HashMap<String, String>().also {
                    copyProperty(it, "indent_size", indentSize, sourceEditorConfig)
                    copyProperty(it, "continuation_indent_size", continuationIndentSize, sourceEditorConfig)
                    copyProperty(it, "max_line_length", maxLineLength, sourceEditorConfig)
                    copyProperty(it, "insert_final_newline", insertFinalNewline, sourceEditorConfig)
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
