package dev.detekt.gradle.report

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

private const val TAB = "\t"

class XmlReportMergerSpec {

    @Test
    fun `passes for no overlapping errors`() {
        val file1 = File.createTempFile("detekt1", "xml").apply {
            writeText(
                """
                    <?xml version="1.0" encoding="utf-8"?>
                    <checkstyle version="4.3">
                    <file name="Sample1.kt">
                    $TAB<error line="1" column="1" severity="warning" message="TestMessage" source="detekt.id_a" />
                    </file>
                    </checkstyle>
                """.trimIndent()
            )
        }
        val file2 = File.createTempFile("detekt2", "xml").apply {
            writeText(
                """
                    <?xml version="1.0" encoding="utf-8"?>
                    <checkstyle version="4.3">
                    <file name="Sample2.kt">
                    $TAB<error line="1" column="1" severity="warning" message="TestMessage" source="detekt.id_b" />
                    </file>
                    </checkstyle>
                """.trimIndent()
            )
        }
        val output = File.createTempFile("output", "xml")
        XmlReportMerger.merge(setOf(file1, file2), output)

        val expectedText = """
            <?xml version="1.0" encoding="UTF-8"?><checkstyle version="4.3">
              <file name="Sample1.kt">
                <error column="1" line="1" message="TestMessage" severity="warning" source="detekt.id_a"/>
              </file>
              <file name="Sample2.kt">
                <error column="1" line="1" message="TestMessage" severity="warning" source="detekt.id_b"/>
              </file>
            </checkstyle>
        """.trimIndent()
        assertThat(output.readText()).isEqualToIgnoringNewLines(expectedText)
    }

    @Test
    fun `passes for all overlapping errors`() {
        val text = """
            <?xml version="1.0" encoding="utf-8"?>
            <checkstyle version="4.3">
            <file name="Sample1.kt">
            $TAB<error line="1" column="1" severity="warning" message="TestMessage" source="detekt.id_a" />
            </file>
            </checkstyle>
        """.trimIndent()
        val file1 = File.createTempFile("detekt1", "xml").apply {
            writeText(text)
        }
        val file2 = File.createTempFile("detekt2", "xml").apply {
            writeText(text)
        }
        val output = File.createTempFile("output", "xml")
        XmlReportMerger.merge(setOf(file1, file2), output)

        val expectedText = """
            <?xml version="1.0" encoding="UTF-8"?><checkstyle version="4.3">
              <file name="Sample1.kt">
                <error column="1" line="1" message="TestMessage" severity="warning" source="detekt.id_a"/>
              </file>
            </checkstyle>
        """.trimIndent()
        assertThat(output.readText()).isEqualToIgnoringNewLines(expectedText)
    }

    @Test
    fun `passes for some overlapping errors`() {
        val file1 = File.createTempFile("detekt1", "xml").apply {
            writeText(
                """
                    <?xml version="1.0" encoding="utf-8"?>
                    <checkstyle version="4.3">
                    <file name="Sample1.kt">
                    $TAB<error line="1" column="1" severity="warning" message="TestMessage" source="detekt.id_a" />
                    </file>
                    <file name="Sample2.kt">
                    $TAB<error line="1" column="1" severity="warning" message="TestMessage" source="detekt.id_b" />
                    </file>
                    </checkstyle>
                """.trimIndent()
            )
        }
        val file2 = File.createTempFile("detekt2", "xml").apply {
            writeText(
                """
                    <?xml version="1.0" encoding="utf-8"?>
                    <checkstyle version="4.3">
                    <file name="Sample2.kt">
                    $TAB<error line="1" column="1" severity="warning" message="TestMessage" source="detekt.id_b" />
                    </file>
                    </checkstyle>
                """.trimIndent()
            )
        }
        val output = File.createTempFile("output", "xml")
        XmlReportMerger.merge(setOf(file1, file2), output)

        val expectedText = """
            <?xml version="1.0" encoding="UTF-8"?><checkstyle version="4.3">
              <file name="Sample1.kt">
                <error column="1" line="1" message="TestMessage" severity="warning" source="detekt.id_a"/>
              </file>
              <file name="Sample2.kt">
                <error column="1" line="1" message="TestMessage" severity="warning" source="detekt.id_b"/>
              </file>
            </checkstyle>
        """.trimIndent()
        assertThat(output.readText()).isEqualToIgnoringNewLines(expectedText)
    }
}
