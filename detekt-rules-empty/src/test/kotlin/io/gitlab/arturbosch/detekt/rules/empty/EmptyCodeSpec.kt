package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

private const val ALLOWED_EXCEPTION_NAME_REGEX = "allowedExceptionNameRegex"

class EmptyCodeSpec {

    private val regexTestingCode = """
        fun f() {
            try {
            } catch (foo: Exception) {
            }
        }
    """.trimIndent()

    @Test
    fun findsEmptyCatch() {
        test { EmptyCatchBlock(Config.empty) }
    }

    @Test
    fun findsEmptyNestedCatch() {
        val code = """
            fun f() {
                try {
                } catch (ignore: Exception) {
                    try {
                    } catch (e: Exception) {
                    }
                }
            }
        """.trimIndent()
        assertThat(EmptyCatchBlock(Config.empty).lint(code)).hasSize(1)
    }

    @Test
    fun doesNotReportIgnoredOrExpectedException() {
        val code = """
            fun f() {
                try {
                } catch (ignore: IllegalArgumentException) {
                } catch (expected: Exception) {
                }
            }
        """.trimIndent()
        assertThat(EmptyCatchBlock(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun doesNotReportEmptyCatchWithConfig() {
        val code = """
            fun f() {
                try {
                } catch (foo: Exception) {
                }
            }
        """.trimIndent()
        val config = TestConfig(ALLOWED_EXCEPTION_NAME_REGEX to "foo")
        assertThat(EmptyCatchBlock(config).lint(code)).isEmpty()
    }

    @Test
    fun findsEmptyFinally() {
        test { EmptyFinallyBlock(Config.empty) }
    }

    @Test
    fun findsEmptyIf() {
        test { EmptyIfBlock(Config.empty) }
    }

    @Test
    fun findsEmptyElse() {
        test { EmptyElseBlock(Config.empty) }
    }

    @Test
    fun findsEmptyFor() {
        test { EmptyForBlock(Config.empty) }
    }

    @Test
    fun findsEmptyWhile() {
        test { EmptyWhileBlock(Config.empty) }
    }

    @Test
    fun findsEmptyDoWhile() {
        test { EmptyDoWhileBlock(Config.empty) }
    }

    @Test
    fun findsEmptyFun() {
        test { EmptyFunctionBlock(Config.empty) }
    }

    @Test
    fun findsEmptyClass() {
        test { EmptyClassBlock(Config.empty) }
    }

    @Test
    fun findsEmptyTry() {
        test { EmptyTryBlock(Config.empty) }
    }

    @Test
    fun findsEmptyWhen() {
        test { EmptyWhenBlock(Config.empty) }
    }

    @Test
    fun findsEmptyInit() {
        test { EmptyInitBlock(Config.empty) }
    }

    @Test
    fun findsOneEmptySecondaryConstructor() {
        test { EmptySecondaryConstructor(Config.empty) }
    }

    @Test
    fun `reports an empty kotlin file`() {
        val rule = EmptyKotlinFile(Config.empty)
        assertThat(rule.lint("", compile = false)).hasSize(1)
    }

    @Test
    fun doesFailWithInvalidRegex() {
        val config = TestConfig(ALLOWED_EXCEPTION_NAME_REGEX to "*foo")
        assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
            EmptyCatchBlock(config).lint(regexTestingCode)
        }
    }
}

// Each Empty* Rule is tested on the same code to make sure they're all detecting distinct problems.
@Suppress("LongMethod")
private fun test(block: () -> Rule) {
    val code = """
        class Empty : Runnable {
        
            init {
        
            }
        
            constructor() {
        
            }
        
            override fun run() {
        
            }
        
            fun stuff() {
                try {
        
                } catch (e: Exception) {
        
                } catch (e: Exception) {
                    //no-op
                } catch (e: Exception) {
                    println()
                } catch (ignored: Exception) {
        
                } catch (expected: Exception) {
        
                } catch (_: Exception) {
        
                } finally {
        
                }
                if (true) {
        
                } else {
        
                }
                when (true) {
        
                }
                for (i in 1..10) {
        
                }
                while (true) {
        
                }
                do {
        
                } while (true)
            }
        }
        
        class EmptyClass() {}
    """.trimIndent()
    val rule = block()
    val findings = rule.lint(code, compile = false)
    assertThat(findings).hasSize(1)
}
