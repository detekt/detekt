package dev.detekt.generator.collection

/**
 * Processes text replacement macros in configuration descriptions.
 *
 * Macros use the syntax `{{MACRO_NAME}}` and are replaced during documentation generation.
 */
class TextReplacementMacro {
    private val macros = mapOf(
        "FUNCTION_MATCHER_DOCS" to FunctionMatcherDocs.FUNCTION_MATCHER_DOCS
    )

    private val macroPattern = """\{\{([A-Z_]+)\}\}""".toRegex()

    fun expand(input: String): String {
        if (input.isEmpty() || !input.contains("{{")) return input

        var result = input
        macroPattern.findAll(input).forEach { match ->
            val macroName = match.groupValues[1]
            val replacement = macros[macroName]
                ?: throw IllegalArgumentException(
                    "Undefined macro: '$macroName'. Available macros: ${availableMacros().joinToString()}"
                )
            result = result.replace(match.value, replacement)
        }
        return result
    }

    fun availableMacros(): Set<String> = macros.keys
}
