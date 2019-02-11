package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TEST_FILENAME
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class FunctionNamingSpec : Spek({

    val fileName = TEST_FILENAME

    describe("FunctionNaming rule") {

        it("allows FunctionName as alias for suppressing") {
            val code = """
			@Suppress("FunctionName")
			fun MY_FUN() = TODO()
		"""
            assertThat(FunctionNaming().lint(code)).isEmpty()
        }

        it("ignores functions in classes matching excludeClassPattern") {
            val code = """
			class WhateverTest {
				fun SHOULD_NOT_BE_FLAGGED() = TODO()
			}
		"""
            val config = TestConfig(mapOf("excludeClassPattern" to ".*Test$"))
            assertThat(FunctionNaming(config).lint(code)).isEmpty()
        }

        it("flags functions inside functions") {
            val code = """
			override fun shouldNotBeFlagged() {
				fun SHOULD_BE_FLAGGED() { }
			}
		"""
            assertThat(FunctionNaming().lint(code)).hasLocationStrings(
                    "'fun SHOULD_BE_FLAGGED() { }' at (2,2) in /$fileName"
            )
        }

        it("ignores overridden functions by default") {
            val code = """
			override fun SHOULD_NOT_BE_FLAGGED() = TODO()
		"""
            assertThat(FunctionNaming().lint(code)).isEmpty()
        }

        it("flags functions with bad names inside overridden functions by default") {
            val code = """
			override fun SHOULD_NOT_BE_FLAGGED() {
				fun SHOULD_BE_FLAGGED() {}
			}
		"""
            assertThat(FunctionNaming().lint(code)).hasLocationStrings(
                    "'fun SHOULD_BE_FLAGGED() {}' at (2,2) in /$fileName"
            )
        }

        it("doesn't ignore overridden functions if ignoreOverridden is false") {
            val code = """
			override fun SHOULD_BE_FLAGGED() = TODO()
		"""
            val config = TestConfig(mapOf("ignoreOverridden" to "false"))
            assertThat(FunctionNaming(config).lint(code)).hasLocationStrings(
                    "'override fun SHOULD_BE_FLAGGED() = TODO()' at (1,1) in /$fileName"
            )
        }
    }
})
