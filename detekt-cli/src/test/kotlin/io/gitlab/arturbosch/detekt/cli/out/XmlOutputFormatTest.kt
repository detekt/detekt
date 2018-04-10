package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.cli.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class XmlOutputFormatTest {

	private val entity1 = Entity("Sample1", "com.sample.Sample1", "",
			Location(SourceLocation(11, 1), TextLocation(0, 10),
					"abcd", "src/main/com/sample/Sample1.kt"))
	private val entity2 = Entity("Sample2", "com.sample.Sample2", "",
			Location(SourceLocation(22, 2), TextLocation(0, 20),
					"efgh", "src/main/com/sample/Sample2.kt"))

	private val outputFormat = XmlOutputReport()

	@Test
	fun renderEmpty() {
		val result = outputFormat.render(TestDetektion())

		assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n</checkstyle>")
	}

	@Test
	fun renderOneForSingleFile() {
		val smell = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")

		val result = outputFormat.render(TestDetektion(smell))

		assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n</file>\n</checkstyle>")
	}

	@Test
	fun renderTwoForSingleFile() {
		val smell1 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")
		val smell2 = CodeSmell(Issue("id_b", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")

		val result = outputFormat.render(TestDetektion(smell1, smell2))

		assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_b\" />\n</file>\n</checkstyle>")
	}

	@Test
	fun renderOneForMultipleFiles() {
		val smell1 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")
		val smell2 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity2, message = "")

		val result = outputFormat.render(TestDetektion(smell1, smell2))

		assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n</file>\n<file name=\"src/main/com/sample/Sample2.kt\">\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n</file>\n</checkstyle>")
	}

	@Test
	fun renderTwoForMultipleFiles() {
		val smell1 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")
		val smell2 = CodeSmell(Issue("id_b", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity1, message = "")
		val smell3 = CodeSmell(Issue("id_a", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity2, message = "")
		val smell4 = CodeSmell(Issue("id_b", Severity.CodeSmell, "", Debt.TWENTY_MINS), entity2, message = "")

		val result = outputFormat.render(TestDetektion(smell1, smell2, smell3, smell4))

		assertThat(result).isEqualTo("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<checkstyle version=\"4.3\">\n<file name=\"src/main/com/sample/Sample1.kt\">\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n\t<error line=\"11\" column=\"1\" severity=\"warning\" message=\"\" source=\"detekt.id_b\" />\n</file>\n<file name=\"src/main/com/sample/Sample2.kt\">\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"\" source=\"detekt.id_a\" />\n\t<error line=\"22\" column=\"2\" severity=\"warning\" message=\"\" source=\"detekt.id_b\" />\n</file>\n</checkstyle>")
	}
}
