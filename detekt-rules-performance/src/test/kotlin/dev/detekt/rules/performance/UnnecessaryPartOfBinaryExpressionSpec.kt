package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class UnnecessaryPartOfBinaryExpressionSpec {

    @Test
    fun `Verify if condition with several arguments`() {
        val code = """
            fun bar() {
                val foo = true
                val baz = false
                if (foo || baz || foo) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `No report several arguments with object-foo math operator and bool val`() {
        val code = """
            class Bar(val bar: Boolean)
            fun bar() {
                val foo = true
                val baz = 10
                val bar = Bar(true)
            
                if (baz < 10 || foo || bar.bar || baz > 10) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report several arguments with object-foo math operator and bool val`() {
        val code = """
            class Bar(val bar: Boolean)
            fun bar() {
                val foo = true
                val baz = 10
                val bar = Bar(true)
            
                if (baz < 10 || foo || bar.bar || baz > 10 || baz < 10) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Not report error if condition contains different operators`() {
        val code = """
            fun bar() {
                val foo = true
                val baz = false
                if (foo || baz && foo) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Not report error if condition contains different operators with other binary expression`() {
        val code = """
            fun bar() {
                val foo = 5
                val baz = false
                if (foo < 5 || baz && foo > 5) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `Report error if condition contains different operators`() {
        val code = """
            fun bar() {
                val foo = true
                val baz = false
                if (foo || baz && baz) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).singleElement().hasTextLocation("baz && baz")
    }

    @Test
    fun `verify foo or foo detected`() {
        val code = """
            fun bar() {
                val foo = true
                if (foo || foo) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify object-foo or object-foo or object-bar detected`() {
        val code = """
            class Bar(val bar: Boolean, val baz: Boolean)
            fun bar() {
                val bar = Bar(true, true)
            
                if (bar.bar || bar.baz || bar.bar) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify object-foo or object-foo detected`() {
        val code = """
            class Bar(val bar: Boolean)
            fun bar() {
                val bar = Bar(true)
            
                if (bar.bar || bar.bar) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify foo more 5 and foo more 5 detected`() {
        val code = """
            fun bar() {
                val foo = 1
                if (foo > 1 && foo > 1) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify foo more 5 && foo more 5 detected un trim`() {
        val code = """
            fun bar() {
                val foo = 1
                if (foo> 1 && foo >1) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify object-foo && object-foo detected`() {
        val code = """
            class Bar(val bar: Boolean)
            
            fun bar() {
                val bar = Bar(true)
            
                if (bar.bar && bar.bar) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify foo does not report`() {
        val code = """
            fun bar() {
                val foo = true
                if (foo) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `verify more and less if works as expected`() {
        val code = """
            fun bar() {
                val foo = 0
                val bar = 1
                if (foo > bar || foo > 1) {
                    //TODO
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `verify into filter function`() {
        val code = """
            fun bar() {
                val list = listOf<Int>()
            
                list.filter { it > 1 || it > 1 }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify into when`() {
        val code = """
            fun bar() {
                val foo = true
                when {
                    foo || foo -> {
                    }
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify two or into when`() {
        val code = """
            fun bar() {
                val foo = true
                val bar = true
                when {
                    foo || bar || foo -> {
                    }
                }
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `Don't raise issues with pair creation`() {
        val code = """
            fun foo() {
                1 to 1
            }
        """.trimIndent()

        val findings = UnnecessaryPartOfBinaryExpression(Config.empty).lint(code)
        assertThat(findings).isEmpty()
    }
}
