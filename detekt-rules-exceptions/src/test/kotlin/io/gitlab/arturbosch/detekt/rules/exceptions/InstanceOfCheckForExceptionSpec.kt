package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class InstanceOfCheckForExceptionSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { InstanceOfCheckForException() }

    describe("InstanceOfCheckForException rule") {

        it("has is and as checks") {

            val code = """
                fun x() {
                    try {
                    } catch(e: Exception) {
                        if (e is IllegalArgumentException || (e as IllegalArgumentException) != null) {
                            return
                        }
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        it("has nested is and as checks") {
            val code = """
                fun x() {
                    try {
                    } catch(e: Exception) {
                        if (1 == 1) {
                            val b = e !is IllegalArgumentException || (e as IllegalArgumentException) != null
                        }
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }

        it("has no instance of check") {
            val code = """
                fun x() {
                    try {
                    } catch(e: Exception) {
                        val s = ""
                        if (s is String || (s as String) != null) {
                            val other: Exception? = null
                            val b = other !is IllegalArgumentException || (other as IllegalArgumentException) != null
                        }
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("has no checks for the subtype of an exception") {
            val code = """
                interface I
                
                fun foo() {
                    try {
                    } catch(e: Exception) {
                        if (e is I || (e as I) != null) {
                        }
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
