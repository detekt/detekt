package io.gitlab.arturbosch.detekt.output

import java.io.File
import java.nio.file.Files

/**
 * Merges the content of input with the content of target.
 *
 * @author Markus Schwarz
 */
internal fun mergeXmlReports(target: File, input: List<File>) {
	check(target.exists() && target.isFile) { "$target does not exist" }
	if (input.isEmpty()) return

	val content = readContent(target).toMutableList()

	input.forEach { file ->
		content.addAll(readContent(file))
	}

	val startOfFile = listOf("""<?xml version="1.0" encoding="utf-8"?>""", """<checkstyle version="4.3">""")
	val endOfFile = listOf("</checkstyle>")
	val data = (startOfFile + content + endOfFile).joinToString(lineSeparator)
	Files.write(target.toPath(), data.toByteArray())
}

/**
 * Reads the actual content of a detekt xml report file where the actual content starts on line 3.
 *
 * <pre>
 *     <?xml version="1.0" encoding="utf-8"?>
 *     <checkstyle version="4.3">
 *       <file name="/path/to/File.kt">
 *         <error line="6" column="29" severity="warning" message="Smell Description" source="smell" />
 *       </file>
 *     </checkstyle>%
 * </pre>
 *
 * @author Markus Schwarz
 */
internal fun readContent(file: File): List<String> {
	check(file.exists() && file.isFile) { "$file does not exist" }
	return file.bufferedReader().use { it.readLines() }.drop(2).dropLast(1)
}

private val lineSeparator = System.getProperty("line.separator")
