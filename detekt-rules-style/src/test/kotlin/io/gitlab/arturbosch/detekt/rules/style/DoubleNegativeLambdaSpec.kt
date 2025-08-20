package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.ValueWithReason
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import dev.detekt.test.toConfig
import org.junit.jupiter.api.Test

class DoubleNegativeLambdaSpec {

    private val subject = DoubleNegativeLambda(Config.empty)

    @Test
    fun `reports simple logical not`() {
        val code = """
            import kotlin.random.Random
            fun Int.isEven() = this % 2 == 0
            val rand = Random.Default.nextInt().takeUnless { !it.isEven() }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports logical not in binary expression`() {
        val code = """
            import kotlin.random.Random
            fun Int.isEven() = this % 2 == 0
            val rand = Random.Default.nextInt().takeUnless { it > 0 && !it.isEven() }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports logical not prefixing brackets`() {
        val code = """
            import kotlin.random.Random
            fun Int.isEven() = this % 2 == 0
            val rand = Random.Default.nextInt().takeUnless { !(it == 0 || it.isEven()) }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports negation inside nested lambda`() {
        val code = """
            import kotlin.random.Random
            fun Int.isEven() = this % 2 == 0
            val rand = Random.Default.nextInt().takeUnless { it.isEven().takeIf { i -> !i } ?: false }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports double nested negation`() {
        val code = """
            import kotlin.random.Random
            fun Int.isEven() = this % 2 == 0
            val rand = Random.Default.nextInt().takeUnless { it.isEven().takeUnless { i -> !i } ?: false }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).hasSize(2)
        assertThat(findings).element(0)
            .hasSourceLocation(3, 62) // second takeUnless
        assertThat(findings).element(1)
            .hasSourceLocation(3, 37) // first takeUnless
    }

    @Test
    fun `reports function with 'not' in the name`() {
        val code = """
            import kotlin.random.Random
            fun Int.isNotZero() = this != 0
            val rand = Random.Default.nextInt().takeUnless { it.isNotZero() }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports zero-param function with 'non' in the name`() {
        val code = """
            import kotlin.random.Random
            fun Int.isNonNegative() = 0 < this
            val rand = Random.Default.nextInt().takeUnless { it.isNonNegative() }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports single-param function with 'non' in the name`() {
        val code = """
            import kotlin.random.Random            
            fun Int.isNotGreaterThan(other: Int) = other < this
            val rand = Random.Default.nextInt().takeUnless { it.isNotGreaterThan(0) }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports not equal`() {
        val code = """
            import kotlin.random.Random
            val rand = Random.Default.nextInt().takeUnless { it != 0 }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports not equal by reference`() {
        val code = """
            import kotlin.random.Random
            val rand = Random.Default.nextInt().takeUnless { it !== 0 }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports !in`() {
        val code = """
            import kotlin.random.Random
            val rand = Random.Default.nextInt().takeUnless { it !in 1..3 }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports !is`() {
        val code = """
            val list = listOf(3, "a", true)
            val maybeBoolean = list.firstOrNull().takeUnless { it !is Boolean }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports use of operator fun not`() {
        val code = """
            import kotlin.random.Random
            val rand = Random.Default.nextInt().takeUnless { (it > 0).not() }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report function with 'not' in part of the name`() {
        val code = """
            fun String.hasAnnotations() = this.contains("annotations")
            val nonAnnotated = "".takeUnless { it.hasAnnotations() }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report non-null assert in takeUnless`() {
        val code = """
            val x = "".takeUnless { it!!.isEmpty() }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports double negatives with none by default`() {
        val code = """
            val isAllEven = listOf(1, 2, 3).none { it % 2 != 0 }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports negative function name from config`() {
        val config =
            TestConfig(
                DoubleNegativeLambda.NEGATIVE_FUNCTIONS to listOf(
                    ValueWithReason(value = "filterNot", reason = "Use `filter` instead.").toConfig(),
                )
            )
        val code = """
            fun Int.isEven() = this % 2 == 0
            val isValid = listOf(1, 2, 3).filterNot { !it.isEven() }
        """.trimIndent()

        assertThat(DoubleNegativeLambda(config).lint(code)).hasSize(1)
    }

    @Test
    fun `reports negative function name parts from config`() {
        val config = TestConfig(DoubleNegativeLambda.NEGATIVE_FUNCTION_NAME_PARTS to listOf("isnt"))
        val code = """
            import kotlin.random.Random
            fun Int.isntOdd() = this % 2 == 0
            val rand = Random.Default.nextInt().takeUnless { it.isntOdd() }
        """.trimIndent()

        assertThat(DoubleNegativeLambda(config).lint(code)).hasSize(1)
    }

    @Test
    fun `reports multiple negations in message`() {
        val code = """
            import kotlin.random.Random
            val list: List<Int> = listOf(1, 2, 3)
            val rand = Random.Default.nextInt().takeUnless { it !in list && it != 0 }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage("Double negative through using `!in`, `!=` inside a `takeUnless` lambda. Use `takeIf` instead.")
        assertThat(findings).singleElement()
            .hasStartSourceLocation(3, 37)
            .hasEndSourceLocation(3, 74)
    }

    @Test
    fun `report for negative function with no positive counterpart`() {
        val config =
            TestConfig(
                DoubleNegativeLambda.NEGATIVE_FUNCTIONS to listOf(
                    ValueWithReason(value = "never", reason = null).toConfig(),
                )
            )
        val code = """
            fun <T> List<T>.never(predicate: (T) -> Boolean) = this.none(predicate)
            val list = listOf(1, 2, 3)
            val result = list.never { it != 0 }
        """.trimIndent()

        val findings = DoubleNegativeLambda(config).lint(code)
        assertThat(findings).singleElement()
            .hasMessage("Double negative through using `!=` inside a `never` lambda. Rewrite in the positive.")
    }
}
