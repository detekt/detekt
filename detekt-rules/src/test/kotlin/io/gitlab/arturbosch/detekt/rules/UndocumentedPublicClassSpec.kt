package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.documentation.UndocumentedPublicClass
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class UndocumentedPublicClassSpec : SubjectSpek<UndocumentedPublicClass>({
	subject { UndocumentedPublicClass() }

	it("finds two undocumented classes") {
		subject.lint(Case.Comments.path())
		assertThat(subject.findings).hasSize(3)
	}

	it("should report missing doc over object decl") {
		assertThat(subject.lint(""" object o { } """)).hasSize(1)
	}

	it("should not report for documented public object") {
		val file = """
/**
 * Class docs not being recognized.
 */
object Main {
    /**
     * The entry point for the application.
     *
     * @param args The list of process arguments.
     */
    @JvmStatic
    fun main(args: Array<String>) {
    }
}
"""

		assertThat(subject.lint(file)).isEmpty()
	}

})