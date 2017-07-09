package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Java6Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class TodoCommentSpec : Spek({

	given("a kt file containing // TODO:") {
		it("should flag it") {
			assertThat(TodoComment().lint(compileContentForTest("""
        package something

        class Test {
          // TODO: test
        }
      """).text)).hasSize(1)
		}
	}

	given("a kt file containing //TODO:") {
		it("should flag it") {
			assertThat(TodoComment().lint(compileContentForTest("""
        package something

        class Test {
          //TODO: test
        }
      """).text)).hasSize(1)
		}
	}

	given("a kt file containing // TODO") {
		it("should not flag it") {
			assertThat(TodoComment().lint(compileContentForTest("""
        package something

        class Test {
          // TODO test
        }
      """).text)).hasSize(0)
		}
	}

})
