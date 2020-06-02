package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object HasPlatformTypeSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { HasPlatformType(Config.empty) }

    describe("Deprecation detection") {

        it("reports when public function returns expression of platform type") {
            val code = """
                class Person {
                    fun apiCall() = System.getProperty("propertyName")
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report when private") {
            val code = """
                class Person {
                    private fun apiCall() = System.getProperty("propertyName")
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report when public function returns expression of platform type and type explicitly declared") {
            val code = """
                class Person {
                    fun apiCall(): String = System.getProperty("propertyName")
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports when property initiated with platform type") {
            val code = """
                class Person {
                    val name = System.getProperty("name")
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report when private") {
            val code = """
                class Person {
                    private val name = System.getProperty("name")
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report when property initiated with platform type and type explicitly declared") {
            val code = """
                class Person {
                    val name: String = System.getProperty("name")
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
