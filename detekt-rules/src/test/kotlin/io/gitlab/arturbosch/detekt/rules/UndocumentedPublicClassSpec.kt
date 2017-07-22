package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.documentation.UndocumentedPublicClass
import io.gitlab.arturbosch.detekt.test.TestConfig
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
		assertThat(subject.lint(Case.Comments.path())).hasSize(3)
	}

	val inner = """
			/** Some doc */
			class TestInner {
				inner class Inner
			}"""

	val innerInterface = """
			/** Some doc */
			class TestInner {
				interface Something
			}"""

	val nested = """
			/** Some doc */
			class TestNested {
				class Nested
			}"""

	it("should report inner classes by default") {
		assertThat(subject.lint(inner)).hasSize(1)
	}

	it("should report inner interfaces by default") {
		assertThat(subject.lint(innerInterface)).hasSize(1)
	}

	it("should report nested classes by default") {
		assertThat(subject.lint(nested)).hasSize(1)
	}

	it("should not report inner classes when turned off") {
		val findings = UndocumentedPublicClass(TestConfig(mapOf(UndocumentedPublicClass.SEARCH_IN_INNER_CLASS to "false"))).lint(inner)
		assertThat(findings).isEmpty()
	}

	it("should not report inner interfaces when turned off") {
		val findings = UndocumentedPublicClass(TestConfig(mapOf(UndocumentedPublicClass.SEARCH_IN_INNER_INTERFACE to "false"))).lint(innerInterface)
		assertThat(findings).isEmpty()
	}

	it("should not report nested classes when turned off") {
		val findings = UndocumentedPublicClass(TestConfig(mapOf(UndocumentedPublicClass.SEARCH_IN_NESTED_CLASS to "false"))).lint(nested)
		assertThat(findings).isEmpty()
	}

	it("should report missing doc over object declaration") {
		assertThat(subject.lint("object o")).hasSize(1)
	}

	it("should not report for documented public object") {
		val code = """
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

		assertThat(subject.lint(code)).isEmpty()
	}

	it("should not report for anonymous objects") {
		val code = """
			fun main(args: Array<String>) {
				recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {}
			}
		"""

		assertThat(subject.lint(code)).isEmpty()
	}

})
