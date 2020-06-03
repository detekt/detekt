package io.gitlab.arturbosch.detekt.formatting

const val INDENT_SIZE_KEY = "indent_size"
const val CONTINUATION_INDENT_SIZE_KEY = "continuation_indent_size"
const val MAX_LINE_LENGTH_KEY = "max_line_length"
const val INSERT_FINAL_NEWLINE_KEY = "insert_final_newline"
const val KOTLIN_IMPORTS_LAYOUT_KEY = "kotlin_imports_layout"

val knownEditorConfigProps = setOf(
    INDENT_SIZE_KEY,
    CONTINUATION_INDENT_SIZE_KEY,
    MAX_LINE_LENGTH_KEY,
    INSERT_FINAL_NEWLINE_KEY,
    KOTLIN_IMPORTS_LAYOUT_KEY
)

const val DEFAULT_INDENT = 4
const val DEFAULT_CONTINUATION_INDENT = 4
const val ANDROID_MAX_LINE_LENGTH = 100
const val DEFAULT_IDEA_LINE_LENGTH = 120
