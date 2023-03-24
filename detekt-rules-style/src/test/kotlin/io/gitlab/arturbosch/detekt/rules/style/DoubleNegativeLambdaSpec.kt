package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class DoubleNegativeLambdaSpec {

    private val subject = DoubleNegativeLambda(Config.empty)

    @Test
    fun `reports simple logical not`() {
        val code = """
            fun Int.isEven() = this % 2 == 0
            val rand = Random.Default.nextInt().takeUnless { !it.isEven() }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports logical not in binary expression`() {
        val code = """
            fun Int.isEven() = this % 2 == 0
            val rand = Random.Default.nextInt().takeUnless { it > 0 && !it.isEven() }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports function with 'not' in the name`() {
        val code = """
            fun Int.isNotZero() = this != 0
            val rand = Random.Default.nextInt().takeUnless { it.isNotZero() }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports zero-param function with 'non' in the name`() {
        val code = """
            fun Int.isNonNegative() = 0 < this
            val rand = Random.Default.nextInt().takeUnless { it.isNonNegative() }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports single-param function with 'non' in the name`() {
        val code = """
            fun Int.isNotGreaterThan(other: Int) = other < this
            val rand = Random.Default.nextInt().takeUnless { it.isNotGreaterThan(0) }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports not equal`() {
        val code = """
            val rand = Random.Default.nextInt().takeUnless { it != 0 }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports not equal by reference`() {
        val code = """
            val rand = Random.Default.nextInt().takeUnless { it !== 0 }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports !in`() {
        val code = """
            val rand = Random.Default.nextInt().takeUnless { it !in 1..3 }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports !is`() {
        val code = """
            val list = listOf(3, "a", true)
            val maybeBoolean = list.firstOrNull().takeUnless { it !is Boolean }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports use of operator fun not`() {
        val code = """
            val rand = Random.Default.nextInt().takeUnless { (it > 0).not() }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report function with 'not' in part of the name`() {
        val code = """
            fun String.hasAnnotations() = this.contains("annotations")
            val nonAnnotated = "".takeUnless { it.hasAnnotations() }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report non-null assert in takeUnless`() {
        val code = """
            val list: List<String?> = listOf("", null, "a")
            val filteredList = list.takeUnless { it!!.isEmpty() }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports function name from config`() {
        val config = TestConfig(DoubleNegativeLambda.NEGATIVE_FUNCTIONS to listOf("none", "filterNot"))
        val code = """
            fun Int.isEven() = this % 2 == 0
            val isValid = list(1, 2, 3).filterNot { !it.isEven() }.none { it != 0 }
        """.trimIndent()

        assertThat(DoubleNegativeLambda(config).compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `reports multiple negations in message`() {
        val code = """
            val list: List<Int> = listOf(1, 2, 3)
            val rand = Random.Default.nextInt().takeUnless { it !in list && it != 0 }
        """.trimIndent()

        val findings = subject.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(2, 37)
        assertThat(findings).hasEndSourceLocation(2, 74)
        assertThat(findings[0]).hasMessage(
            "Double negative through using `!in`, `!=` inside a `takeUnless` lambda. Rewrite in the positive."
        )
    }
}
