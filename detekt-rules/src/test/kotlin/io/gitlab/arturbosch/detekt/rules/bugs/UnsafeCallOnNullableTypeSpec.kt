package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.assertj.core.api.Assertions.assertThat

/**
 * @author Ivan Balaksha
 */
class UnsafeCallOnNullableTypeSpec : SubjectSpek<UnsafeCallOnNullableType>({
    subject { UnsafeCallOnNullableType() }
    describe("check all variants of safe/unsafe calls on nullable types") {
        it("unsafe call on nullable type") {
            val code = """
            class UnsafeCallOnNullable {
                fun test(str: String?) {
                    println(str!!.length)
                }
            }"""
            assertThat(subject.lint(code)).hasSize(1)
        }
        it("safe call on nullable type") {
            val code = """
            class UnsafeCallOnNullable {
                fun test(str: String?) {
                    println(str?.length)
                }
            }"""
            assertThat(subject.lint(code)).hasSize(0)
        }
        it("elvis") {
            val code = """
            class UnsafeCallOnNullable {
                fun test(str: String?) {
                    println(str?.length ?: 0)
                }
            }"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }
})