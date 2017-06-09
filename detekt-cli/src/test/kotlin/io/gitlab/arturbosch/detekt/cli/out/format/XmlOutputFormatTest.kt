package io.gitlab.arturbosch.detekt.cli.out.format

import io.gitlab.arturbosch.detekt.api.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.*

internal class XmlOutputFormatTest {

    private val entity1 = Entity("Sample1", "com.sample.Sample1", "", Location(SourceLocation(11, 1), TextLocation(0, 10), "abcd", "src/main/com/sample/Sample1.kt"))
    private val entity2 = Entity("Sample2", "com.sample.Sample2", "", Location(SourceLocation(22, 2), TextLocation(0, 20), "efgh", "src/main/com/sample/Sample2.kt"))

    private val path = Files.createTempDirectory("reports")
    private val file = path.resolve("report.xml")
    private lateinit var outputFormat: XmlOutputFormat

    @BeforeEach
    fun setUp() {
        outputFormat = XmlOutputFormat(file)
    }

    @Test
    fun renderEmpty() {
        val result = outputFormat.render(Collections.emptyList())

        //language=XML
        assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n</checkstyle>")
    }

    @Test
    fun renderOneForSingleFile() {
        val smell = CodeSmell("id_1", Rule.Severity.CodeSmell, entity1)

        val result = outputFormat.render(listOf(smell))

        //language=XML
        assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"(id_1)\" source=\"detekt.id_1\" />\n</file>\n</checkstyle>")
    }

    @Test
    fun renderTwoForSingleFile() {
        val smell1 = CodeSmell("id_1", Rule.Severity.CodeSmell, entity1)
        val smell2 = CodeSmell("id_2", Rule.Severity.CodeSmell, entity1)

        val result = outputFormat.render(listOf(smell1, smell2))

        //language=XML
        assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"(id_1)\" source=\"detekt.id_1\" />\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"(id_2)\" source=\"detekt.id_2\" />\n</file>\n</checkstyle>")
    }

    @Test
    fun renderOneForMultipleFiles() {
        val smell1 = CodeSmell("id_1", Rule.Severity.CodeSmell, entity1)
        val smell2 = CodeSmell("id_1", Rule.Severity.CodeSmell, entity2)

        val result = outputFormat.render(listOf(smell1, smell2))

        //language=XML
        assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"(id_1)\" source=\"detekt.id_1\" />\n</file>\n<file name=\"src/main/com/sample/Sample2.kt\">\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"(id_1)\" source=\"detekt.id_1\" />\n</file>\n</checkstyle>")
    }

    @Test
    fun renderTwoForMultipleFiles() {
        val smell1 = CodeSmell("id_1", Rule.Severity.CodeSmell, entity1)
        val smell2 = CodeSmell("id_2", Rule.Severity.CodeSmell, entity1)
        val smell3 = CodeSmell("id_1", Rule.Severity.CodeSmell, entity2)
        val smell4 = CodeSmell("id_2", Rule.Severity.CodeSmell, entity2)

        val result = outputFormat.render(listOf(smell1, smell2, smell3, smell4))

        //language=XML
        assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"(id_1)\" source=\"detekt.id_1\" />\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"(id_2)\" source=\"detekt.id_2\" />\n</file>\n<file name=\"src/main/com/sample/Sample2.kt\">\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"(id_1)\" source=\"detekt.id_1\" />\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"(id_2)\" source=\"detekt.id_2\" />\n</file>\n</checkstyle>")
    }
}