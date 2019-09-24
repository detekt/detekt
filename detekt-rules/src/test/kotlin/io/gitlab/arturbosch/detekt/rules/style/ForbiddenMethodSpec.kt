package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForbiddenMethodSpec : Spek({

    describe("ForbiddenMethod rule") {

        it("should report nothing by default") {
            val code = """
            import java.lang.System
            fun main() {
			    System.out.println("hello")
            }
		    """
            val findings = ForbiddenMethod(TestConfig()).compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("should report nothing when methods are blank") {
            val code = """
            import java.lang.System
            fun main() {
			    System.out.println("hello")
            }
		    """
            val findings = ForbiddenMethod(TestConfig(mapOf(ForbiddenMethod.METHODS to "  "))).compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("should report nothing when methods do not match") {
            val code = """
            import java.lang.System
            fun main() {
			    System.out.println("hello")
            }
		    """
            val findings = ForbiddenMethod(
                TestConfig(mapOf(ForbiddenMethod.METHODS to "System.gc"))
            ).compileAndLint(code)
            assertThat(findings).hasSize(0)
        }

        it("should not report System.out.println when methods is println") {
            val code = """
            import java.lang.System
            fun main() {
			    System.out.println("hello")
            }
		    """
            val findings = ForbiddenMethod(
                TestConfig(mapOf(ForbiddenMethod.METHODS to "println"))
            ).compileAndLint(code)
            assertThat(findings).isEmpty()
        }

        it("should report System.out.println when methods is System.out.println") {
            val code = """
            import java.lang.System
            fun main() {
			    System.out.println("hello")
            }
		    """
            val findings = ForbiddenMethod(
                TestConfig(mapOf(ForbiddenMethod.METHODS to "System.out.println"))
            ).compileAndLint(code)
            assertThat(findings).hasLocationStrings("'println(\"hello\")' at (3,12) in /Test.kt")
        }

        it("should report println when methods is println") {
            val code = """
            fun main() {
			    println("hello")
            }
		    """
            val findings = ForbiddenMethod(
                TestConfig(mapOf(ForbiddenMethod.METHODS to "println"))
            ).compileAndLint(code)
            assertThat(findings).isNotEmpty()
        }

        it("should report System.out.println and System.gc when specified in methods") {
            val code = """
            import java.lang.System
            fun main() {
			    System.out.println("hello")
                System.gc()
            }
		    """
            val findings = ForbiddenMethod(
                TestConfig(mapOf(ForbiddenMethod.METHODS to "System.out.println, System.gc"))
            ).compileAndLint(code)
            assertThat(findings)
                .hasSize(2)
                .hasLocationStrings("'gc()' at (4,17) in /Test.kt", "'println(\"hello\")' at (3,12) in /Test.kt")
        }
    }
})
