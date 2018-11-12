package io.gitlab.arturbosch.detekt.output

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.nio.file.Files

private fun fileContent(data: String = "") = """<?xml version="1.0" encoding="utf-8"?>
		|<checkstyle version="4.3">
		|$data</checkstyle>""".trimMargin()

private val emptyContent = fileContent()

internal class XmlReportConsolidationTest : Spek({

	describe("merging invalid report files") {
		it("fails if target file does not exist") {
			assertThatIllegalStateException().isThrownBy {
				mergeXmlReports(File("missing.xml"), emptyList())
			}
		}
		it("fails if any file to merge does not exist") {
			val target = Files.createTempFile("target", ".xml")
			assertThatIllegalStateException().isThrownBy {
				mergeXmlReports(target.toFile(), listOf(File("missing.xml")))
			}
		}
	}

	describe("merging valid report files") {
		it("target file remains unchanged if there are no files to be merged") {
			val target = Files.createTempFile("target", ".xml").toFile()
			target.writeText(emptyContent)

			mergeXmlReports(target, emptyList())

			assertThat(target.readText()).isEqualTo(emptyContent)
		}
		it("data is merged from multiple sources into an empty target file") {
			val content1 = """
				|<file name="Dummy1.kt">
				|	<error line="1" column="2" severity="warning" message="a" source="A" />
				|</file>
				|""".trimMargin()
			val content2 = """
				|<file name="Dummy2.kt">
				|	<error line="2" column="2" severity="warning" message="b" source="B" />
				|	<error line="22" column="22" severity="warning" message="b" source="B" />
				|</file>
				|""".trimMargin()

			val target = Files.createTempFile("target", ".xml").toFile()
			target.writeText(emptyContent)
			val merge1 = Files.createTempFile("merge1", ".xml").toFile()
			merge1.writeText(fileContent(content1))
			val merge2 = Files.createTempFile("merge2", ".xml").toFile()
			merge2.writeText(fileContent(content2))

			mergeXmlReports(target, listOf(merge1, merge2))

			assertThat(target.readText()).isEqualTo(fileContent(content1 + content2))
		}
		it("data is merged from multiple sources into a non empty target file") {
			val targetContent = """
				|<file name="Target.kt">
				|	<error line="1" column="2" severity="warning" message="a" source="A" />
				|</file>
				|""".trimMargin()
			val content1 = """
				|<file name="Dummy1.kt">
				|	<error line="1" column="2" severity="warning" message="a" source="A" />
				|</file>
				|""".trimMargin()
			val content2 = """
				|<file name="Dummy2.kt">
				|	<error line="2" column="2" severity="warning" message="b" source="B" />
				|	<error line="22" column="22" severity="warning" message="b" source="B" />
				|</file>
				|""".trimMargin()

			val target = Files.createTempFile("target", ".xml").toFile()
			target.writeText(fileContent(targetContent))
			val merge1 = Files.createTempFile("merge1", ".xml").toFile()
			merge1.writeText(fileContent(content1))
			val merge2 = Files.createTempFile("merge2", ".xml").toFile()
			merge2.writeText(fileContent(content2))

			mergeXmlReports(target, listOf(merge1, merge2))

			assertThat(target.readText()).isEqualTo(fileContent(targetContent + content1 + content2))
		}
	}
})
