package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RedundantExplicitTypeSpec : Spek({
    val subject by memoized { RedundantExplicitType(Config.empty) }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("RedundantExplicitType") {

        it("reports explicit type for boolean") {
            val code = """
                fun function() {
                    val x: Boolean = true
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports explicit type for integer") {
            val code = """
                fun function() {
                    val x: Int = 3
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports explicit type for long") {
            val code = """
                fun function() {
                    val x: Long = 3L
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports explicit type for float") {
            val code = """
                fun function() {
                    val x: Float = 3.0f
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports explicit type for double") {
            val code = """
                fun function() {
                    val x: Double = 3.0
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports explicit type for char") {
            val code = """
                fun function() {
                    val x: Char = 'f'
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports explicit type for string template") {
            val substitute = "\$x"
            val code = """
                fun function() {
                    val x = 3
                    val y: String = "$substitute"
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports explicit type for name reference expression") {
            val code = """
                object Test

                fun foo() {
                    val o: Test = Test
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("reports explicit type for call expression") {
            val code = """
                interface Person {
                    val firstName: String
                }

                class TallPerson(override val firstName: String, val height: Int): Person

                fun tallPerson() {
                    val t: TallPerson = TallPerson("first", 3)
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("does not report explicit type for call expression when type is an interface") {
            val code = """
                interface Person {
                    val firstName: String
                }

                class TallPerson(override val firstName: String, val height: Int): Person

                fun tallPerson() {
                    val t: Person = TallPerson("first", 3)
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }
    }
})
