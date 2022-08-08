package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions
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
        Assertions.assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify object-foo or object-foo detected`() {
        val code = """
            class Bar(
                val bar: Boolean,
            )
            fun bar() {
                val bar = Bar(true)
                
                if (bar.bar || bar.bar) {
                    //TODO    
                }
            }
            
            
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        Assertions.assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify foo more 5 and foo more 5 detected`() {
        val code = """
            fun bar() {
                val foo = 10
                if (foo > 5 && foo > 5) {
                    //TODO    
                }
            }
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        Assertions.assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify foo more 5 && foo more 5 detected un trim`() {
        val code = """
            fun bar() {
                val foo = 10
                if (foo> 5 && foo >5) {
                    //TODO    
                }
            }
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        Assertions.assertThat(findings).hasSize(1)
    }

    @Test
    fun `verify object-foo && object-foo detected`() {
        val code = """
            fun bar() {
                val bar = Bar(true)
                
                if (bar.bar && bar.bar) {
                    //TODO    
                }
            }
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        Assertions.assertThat(findings).hasSize(1)
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
        Assertions.assertThat(findings).hasSize(0)
    }

    @Test
    fun `verify more and less if works as expected`() {
        val code = """
            fun bar() {
                val foo = 10
                val bar = 50
                if (foo > bar || foo > 150) {
                    //TODO    
                }
            }
            
            
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        Assertions.assertThat(findings).hasSize(0)
    }

    @Test
    fun `verify into filter function`() {
        val code = """
            fun bar() {
                val list = listOf()

                list.filter { it > 10 || it > 10 }
            }
        """
        val findings = UnnecessaryPartOfBinaryExpression().compileAndLint(code)
        Assertions.assertThat(findings).hasSize(1)
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
        Assertions.assertThat(findings).hasSize(1)
    }

}
