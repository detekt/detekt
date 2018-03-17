package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.*
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat

internal class HtmlOutputFormatTest {

	private val outputFormat = HtmlOutputReport()

	@Test
	fun testRenderResultLooksLikeHtml() {
		val result = outputFormat.render(TestDetektion())

		assertThat(result).startsWith("<!DOCTYPE html>\n<html lang=\"en\">")
		assertThat(result).endsWith("</html>\n")

		assertThat(result).contains("<h1>detekt report</h1>")
		assertThat(result).contains("<h2>Metrics</h2>")
		assertThat(result).contains("<h2>Findings</h2>")
	}

	@Test
	fun testRenderResultContainsFileLocations() {
		val result = outputFormat.render(createTestDetektionWithMultipleSmells())

		assertThat(result).contains("<span class=\"location\">\nsrc/main/com/sample/Sample1.kt:11:1\n</span>")
		assertThat(result).contains("<span class=\"location\">\nsrc/main/com/sample/Sample2.kt:22:2\n</span>")
		assertThat(result).contains("<span class=\"location\">\nsrc/main/com/sample/Sample3.kt:33:3\n</span>")
	}

	@Test
	fun testRenderResultContainsRules() {
		val result = outputFormat.render(createTestDetektionWithMultipleSmells())

		assertThat(result).contains("<span class=\"rule\">\nid_a\n</span>")
		assertThat(result).contains("<span class=\"rule\">\nid_a\n</span>")
		assertThat(result).contains("<span class=\"rule\">\nid_a\n</span>")
	}

	@Test
	fun testRenderResultContainsMessages() {
		val result = outputFormat.render(createTestDetektionWithMultipleSmells())

		assertThat(result).contains("<span class=\"message\">\nB1\n</span>")
		assertThat(result).contains("<span class=\"message\">\nB2\n</span>")
		assertThat(result).contains("<span class=\"message\">\nB3\n</span>")
	}

	@Test
	fun testRenderResultContainsDescriptions() {
		val result = outputFormat.render(createTestDetektionWithMultipleSmells())

		assertThat(result).contains("<span class=\"description\">\nA1\n</span>")
		assertThat(result).contains("<span class=\"description\">\nA2\n</span>")
		assertThat(result).contains("<span class=\"description\">\nA3\n</span>")
	}

	private fun createTestDetektionWithMultipleSmells() : Detektion {
		val entity1 = Entity("Sample1", "com.sample.Sample1", "",
				Location(SourceLocation(11, 1), TextLocation(0, 10),
						"abcd", "src/main/com/sample/Sample1.kt"))
		val entity2 = Entity("Sample2", "com.sample.Sample2", "",
				Location(SourceLocation(22, 2), TextLocation(0, 20),
						"efgh", "src/main/com/sample/Sample2.kt"))
		val entity3 = Entity("Sample2", "com.sample.Sample3", "",
				Location(SourceLocation(33, 3), TextLocation(0, 30),
						"ijkl", "src/main/com/sample/Sample3.kt"))

		return TestDetektion(
				CodeSmell(Issue("id_a", Severity.CodeSmell, "A1", Debt.TWENTY_MINS), entity1, message = "B1"),
				CodeSmell(Issue("id_b", Severity.CodeSmell, "A2", Debt.TWENTY_MINS), entity2, message = "B2"),
				CodeSmell(Issue("id_c", Severity.CodeSmell, "A3", Debt.TWENTY_MINS), entity3, message = "B3"))
	}
}
