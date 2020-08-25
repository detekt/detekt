package io.github.detekt.custom

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SpekTestDiscoverySpec : Spek({
    setupKotlinEnvironment()

    val subject by memoized { SpekTestDiscovery() }
    val env: KotlinCoreEnvironment by memoized()

    describe("variable declarations in spek groups should only be simple") {

        context("top level scope") {

            it("allows strings, paths and files by default") {
                val code = createSpekCode("""
                    val s = "simple"
                    val p = Paths.get("")
                    val f = File("")
                """.trimIndent())

                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("detects disallowed types on top level scope") {
                val code = createSpekCode("val s = Any()")

                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("allows memoized blocks") {
                val code = createSpekCode("val s by memoized { Any() }")

                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("describe and context blocks") {

            setOf("describe", "context").forEach { name ->
                it("allows strings, files and paths by default") {
                    val code = createSpekCode("""
                        $name("group") {
                            val s = "simple"
                            val p = Paths.get("")
                            val f = File("")
                            val m by memoized { Any() }
                        }
                    """.trimIndent())

                    assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
                }
            }

            setOf("describe", "context").forEach { name ->
                it("disallows non memoized declarations") {
                    val code = createSpekCode("""
                        $name("group") {
                            val complex = Any()
                        }
                    """.trimIndent())

                    assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
                }
            }
        }
    }
})

private fun createSpekCode(content: String) = """
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.nio.file.Paths

class Test : Spek({
    describe("top") {
        $content
    }
})
""".trimIndent()
