package io.github.detekt.custom

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SpekTestDiscoverySpec : KotlinCoreEnvironmentTest() {
    val subject = SpekTestDiscovery()

    @Nested
    inner class VariableDeclarationsInSpekGroupsShouldOnlyBeSimple {

        @Nested
        inner class TopLevelScope {

            @Test
            fun `allows strings, paths and files by default`() {
                val code = createSpekCode(
                    """
                    val s = "simple"
                    val p = Paths.get("")
                    val f = File("")
                """
                )

                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `detects disallowed types on top level scope`() {
                val code = createSpekCode("val s = Any()")

                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `allows memoized blocks`() {
                val code = createSpekCode("val s by memoized { Any() }")

                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class DescribeAndContextBlocks {

            @ParameterizedTest
            @ValueSource(strings = ["describe", "context"])
            fun `allows strings, files and paths by default`(name: String) {
                val code = createSpekCode(
                    """
                        $name("group") {
                            val s = "simple"
                            val p = Paths.get("")
                            val f = File("")
                            val m by memoized { Any() }
                        }
                    """
                )

                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @ParameterizedTest
            @ValueSource(strings = ["describe", "context"])
            fun `disallows non memoized declarations`(name: String) {
                val code = createSpekCode(
                    """
                        $name("group") {
                            val complex = Any()
                        }
                    """
                )

                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }
        }
    }
}

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
"""
