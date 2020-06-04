package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.core.EditorConfig

/**
 * Creates new EditorConfig by copying the existing EditorConfig with properties values passed by parameters.
 * Values of properties passed by parameters are more important than properties in sourceEditorConfig.
 */
fun EditorConfig?.copy(vararg overrides: Pair<String, Any>): EditorConfig {
    val valuesToOverride = overrides.toMap()
    val newValues = HashMap<String, String>()
    knownEditorConfigProps.forEach { copyProperty(newValues, it, valuesToOverride[it], this) }
    return EditorConfig.fromMap(newValues)
}

private fun copyProperty(
    map: MutableMap<String, String>,
    property: String,
    value: Any?,
    sourceEditorConfig: EditorConfig?
) {
    val newValue: String? = value?.toString() ?: sourceEditorConfig?.get(property)
    newValue?.let { map[property] = it }
}
