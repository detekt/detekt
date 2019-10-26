package io.gitlab.arturbosch.detekt.rules.junit

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class TestWithoutAssertionSpec : Spek({
    describe("junit test without assertion rule") {

        it("should report if test has no assertion") {
            val code = """
                @Test
                fun `should add two numbers`() {
                    val result = add(1, 2)
                }
            """.trimIndent()

            val findings = TestWithoutAssertion().lint(code)

            assertThat(findings).hasSize(1)
        }

        it("should not report if test has an assertion") {
            val code = """
                @Test
                fun `should add two numbers`() {
                    val result = add(1, 2)
                    assertEquals(3, result)
                }
            """.trimIndent()

            val findings = TestWithoutAssertion().lint(code)

            assertThat(findings).isEmpty()
        }

        it("should allow configuring the assertion pattern") {
            val code = """
                @Test
                fun `should display message`() {
                    loadFragment()

                    onView(withId(R.id.message)).check(matches(isDisplayed()))
                }
            """.trimIndent()

            val findings = TestWithoutAssertion(
                TestConfig("assertionPattern" to "onView")
            ).lint(code)

            assertThat(findings).isEmpty()
        }
    }
})
