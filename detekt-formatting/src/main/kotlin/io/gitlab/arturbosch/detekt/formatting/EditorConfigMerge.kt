package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.core.EditorConfig

/**
 * Creates a new [EditorConfig] by copying the existing [EditorConfig] and replacing or adding entries by [overrides].
 */
fun EditorConfig?.copy(overrides: Map<String, Any>): EditorConfig {
    val newValues = HashMap<String, String>()
    knownEditorConfigProps.forEach { copyProperty(newValues, it, overrides[it], this) }
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
