package io.gitlab.arturbosch.detekt.generator.out

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class MarkdownContentSpec : Spek({

	given("some markdown headings") {

		it("prints the correct content") {
			val expected = "# H1\n\n## H2\n\n### H3\n\n#### H4\n"
			val actual = markdown {
				h1 { "H1" }
				h2 { "H2" }
				h3 { "H3" }
				h4 { "H4" }
			}
			assertThat(expected).isEqualTo(actual)
		}
	}

	given("a markdown paragraph") {

		it("prints the correct content") {
			val expected = "asdf\n"
			val actual = markdown {
				paragraph { "asdf" }
			}
			assertThat(expected).isEqualTo(actual)
		}
	}

	given("a markdown ordered list") {

		it("prints the correct content") {
			val expected = "1. first\n2. second"
			val actual = markdown {
				orderedList { listOf("first", "second") }
			}
			assertThat(expected).isEqualTo(actual)
		}
	}
})
