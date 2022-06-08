package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.jupiter.api.Test

class SignaturesSpec {
    private var result = ""
    private val subject = TestRule { result = it }

    @Test
    fun `function with type reference`() {
        subject.compileAndLint(functionWithTypeReference())
        assertThat(result).isEqualTo(signatureWithTypeReference())
    }

    @Test
    fun `function without type reference`() {
        subject.compileAndLint(functionWithoutTypeReference())
        assertThat(result).isEqualTo(signatureWithoutTypeReference())
    }

    @Test
    fun `function throws exception`() {
        assertThatThrownBy {
            subject.compileAndLint(functionWontCompile())
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Error building function signature")
    }
}

private class TestRule(val block: (String) -> Unit) : Rule() {
    override val issue = Issue(javaClass.simpleName, Severity.CodeSmell, "", Debt.TWENTY_MINS)

    override fun visitNamedFunction(function: KtNamedFunction) {
        block(function.buildFullSignature())
    }
}

private fun signatureWithTypeReference() = """
    Test.kt${'$'}TestClass.Companion${'$'}@JvmStatic @Parameterized.Parameters(name = "parameterName") /** * Function KDoc */ fun data(): List<Array<out Any?>>
""".trimIndent()

private fun signatureWithoutTypeReference() = """
    Test.kt${'$'}TestClass.Companion${'$'}@JvmStatic @Parameterized.Parameters(name = "parameterName") /** * Function KDoc */ fun data()
""".trimIndent()

private fun functionWithTypeReference() = """
    @RunWith(Parameterized::class)
    class TestClass {
        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "parameterName")
            /**
             * Function KDoc
             */
            fun data(): List<Array<out Any?>> =    
                /**
                 * more description
                 * more description
                 * more description
                 * more description
                 * more description
                 */
                listOf(
                    arrayOf(1, 2, 3, 4),
                    arrayOf(11, 22, 33, 44)
                )
        }
    }
""".trimIndent()

private fun functionWithoutTypeReference() = """
    @RunWith(Parameterized::class)
    class TestClass {
        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "parameterName")
            /**
             * Function KDoc
             */
            fun data() =
                /**
                 * more description
                 * more description
                 * more description
                 * more description
                 * more description
                 */
                listOf(
                    arrayOf(1, 2, 3, 4),
                    arrayOf(11, 22, 33, 44)
                )
        }
    }
""".trimIndent()

private fun functionWontCompile() = """
    @RunWith(Parameterized.class)
    class TestClass {
        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "parameterName")
            /**
             * Function KDoc
             */
            fun data() =
                /**
                 * more description
                 * more description
                 * more description
                 * more description
                 * more description
                 */
                listOf(
                    arrayOf(1, 2, 3, 4),
                    arrayOf(11, 22, 33, 44)
                )
        }
    }
""".trimIndent()
