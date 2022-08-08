package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UnnecessaryPartOfBinaryExpressionSpec {

    @Test
    fun `verify foo or foo detected`() {
        val code = """
            fun bar() {
                val foo = true
                if (foo || foo) {
                    //TODO    
                }
            }
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
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
            
            
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
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
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
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
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
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
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
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
            
            
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        assertThat(findings).hasSize(0)
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
            
            
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        assertThat(findings).hasSize(0)
    }

    @Test
    fun `verify into filter function`() {
        val code = """
            fun bar() {
                val list = listOf<Int>()

                list.filter { it > 1 || it > 1 }
            }
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
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
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        assertThat(findings).hasSize(1)
    }
}
