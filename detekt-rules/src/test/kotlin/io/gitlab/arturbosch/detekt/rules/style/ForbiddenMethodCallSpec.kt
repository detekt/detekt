package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForbiddenMethodCallSpec : Spek({

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("ForbiddenMethodCall rule") {

        it("should report nothing by default") {
            val code = """
            import java.lang.System
            fun main() {
            System.out.println("hello")
            }
            """
            val findings = ForbiddenMethodCall(TestConfig()).compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }

        it("should report nothing when methods are blank") {
            val code = """
            import java.lang.System
            fun main() {
            System.out.println("hello")
            }
            """
            val findings =
                ForbiddenMethodCall(TestConfig(mapOf(ForbiddenMethodCall.METHODS to "  "))).compileAndLintWithContext(
                    wrapper.env,
                    code
                )
            assertThat(findings).isEmpty()
        }

        it("should report nothing when methods do not match") {
            val code = """
            import java.lang.System
            fun main() {
            System.out.println("hello")
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(ForbiddenMethodCall.METHODS to listOf("java.lang.System.gc")))
            ).compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).isEmpty()
        }

        it("should report method call when using the fully qualified name") {
            val code = """
            fun main() {
            java.lang.System.out.println("hello")
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(ForbiddenMethodCall.METHODS to listOf("java.io.PrintStream.println")))
            ).compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(13 to 50)
        }

        it("should report method call when not using the fully qualified name") {
            val code = """
            import java.lang.System.out
            fun main() {
            out.println("hello")
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(ForbiddenMethodCall.METHODS to listOf("java.io.PrintStream.println")))
            ).compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(41 to 61)
        }

        it("should report multiple different methods") {
            val code = """
            import java.lang.System
            fun main() {
            System.out.println("hello")
                System.gc()
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(ForbiddenMethodCall.METHODS to listOf("java.io.PrintStream.println", "java.lang.System.gc")))
            ).compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(37 to 64, 69 to 80)
        }

        it("should report multiple different methods config with sting") {
            val code = """
            import java.lang.System
            fun main() {
            System.out.println("hello")
                System.gc()
            }
            """
            val findings = ForbiddenMethodCall(
                TestConfig(mapOf(ForbiddenMethodCall.METHODS to "java.io.PrintStream.println, java.lang.System.gc"))
            ).compileAndLintWithContext(wrapper.env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(37 to 64, 69 to 80)
        }
    }
})
