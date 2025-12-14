package dev.detekt.metrics.processors.util

object LLOC {

    private val comments = arrayOf("//", "/*", "*/", "*")
    private val escapes = arrayOf("import", "package")

    fun analyze(lines: List<String>, isCommentMode: Boolean = false, isFullMode: Boolean = false): Int =
        LLOCCounter(lines, isCommentMode, isFullMode).run()

    private class LLOCCounter(
        private val lines: List<String>,
        private val isCommentMode: Boolean = false,
        private val isFullMode: Boolean = false,
    ) {

        private var counter = 0
        private var openedBrackets = 0
        private var closedBrackets = 0
        private var escape: Boolean = false

        @Suppress("LoopWithTooManyJumpStatements")
        fun run(): Int {
            for (line in lines) {
                val trimmed = line.trim()

                if (trimmed.isEmpty()) {
                    continue
                }

                countOrEscapeComment(trimmed)
                if (escape) {
                    continue
                }

                countOrEscapeAdditionalStatements(trimmed)
                if (escape) {
                    continue
                }

                countStatementsAndDeclarations(trimmed)
            }

            return counter + if (openedBrackets - closedBrackets == 0) openedBrackets else -1
        }

        private fun countStatementsAndDeclarations(trimmed: String) {
            if (trimmed.contains(";")) {
                counter++
            }

            if (trimmed.contains("{")) {
                openedBrackets += frequency(trimmed, "{")
            } else if (trimmed.length != 1) {
                counter++
            }

            if (trimmed.contains("}")) {
                closedBrackets += frequency(trimmed, "}")
            }
        }

        private fun countOrEscapeAdditionalStatements(trimmed: String) {
            escape = isEscaped(trimmed, escapes)
            if (escape && isFullMode) {
                counter++
            }
        }

        private fun countOrEscapeComment(trimmed: String) {
            escape = isEscaped(trimmed, comments)
            if (isCommentMode && escape) {
                counter++
            }
        }

        private fun isEscaped(trimmed: String, rules: Array<String>): Boolean = rules.any { trimmed.startsWith(it) }

        private fun frequency(source: String, part: String): Int {
            if (source.isEmpty() || part.isEmpty()) {
                return 0
            }

            var count = 0
            var pos = source.indexOf(part, 0)
            while (pos != -1) {
                pos += part.length
                count++
                pos = source.indexOf(part, pos)
            }

            return count
        }
    }
}
