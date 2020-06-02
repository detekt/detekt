package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DeprecationSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { Deprecation(Config.empty) }

    describe("Deprecation detection") {

        it("reports when supertype is deprecated") {
            val code = """
                @Deprecated("deprecation message")
                abstract class Foo {
                    abstract fun bar() : Int

                    fun baz() {
                    }
                }

                abstract class Oof : Foo() {
                    fun spam() {
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report when supertype is not deprecated") {
            val code = """
                abstract class Oof : Foo() {
                    fun spam() {
                    }
                }
                abstract class Foo {
                    abstract fun bar() : Int

                    fun baz() {
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
