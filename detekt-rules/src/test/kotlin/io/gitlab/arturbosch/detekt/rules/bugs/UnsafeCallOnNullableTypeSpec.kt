package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnsafeCallOnNullableTypeSpec : Spek({
    val subject by memoized { UnsafeCallOnNullableType() }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("check all variants of safe/unsafe calls on nullable types") {

        it("reports unsafe call on nullable type") {
            val code =
                """
                fun test(str: String?) {
                    println(str!!.length)
                }
                """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("does not report unsafe call on platform type") {
            val code =
                """
                import java.util.UUID

                val version = UUID.randomUUID()!!
                """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("does not report safe call on nullable type") {
            val code =
                """
                fun test(str: String?) {
                    println(str?.length)
                }
                """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("does not report safe call in combination with the elvis operator") {
            val code =
                """
                fun test(str: String?) {
                    println(str?.length ?: 0)
                }
                """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }
    }
})
