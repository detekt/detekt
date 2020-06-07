package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class IsPropertyNamingSpec : Spek({

    val subject by memoized { IsPropertyNaming() }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("IsPropertyNaming rule") {

        context("property declarations") {
            it("should not detect Boolean property") {
                val code = """
                data class O (
                    var isDefault: Boolean
                )
            """
                val findings = subject.compileAndLintWithContext(wrapper.env, code)

                assertThat(findings).isEmpty()
            }

            it("should warn about primitive properties") {
                val code = """
                data class O (
                    var isDefault: Int
                )
            """
                val findings = subject.compileAndLintWithContext(wrapper.env, code)

                assertThat(findings).hasSize(1)
            }

            it("should warn about inner classes") {
                val code = """
                data class O (
                    var isDefault: Inner
                ) {
                    class Inner
                }
            """
                val findings = subject.compileAndLintWithContext(wrapper.env, code)

                assertThat(findings).isEmpty()
            }
        }
    }
})
