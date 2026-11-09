package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class WhenIndyDirectCandidateSpec(val env: KotlinEnvironmentContainer) {

    private val rule = WhenIndyDirectCandidate(Config.empty)

    @Test
    fun `reports positive type checks against one subject`() {
        val findings = lint(
            """
            fun classify(value: Any?) = when {
                value is String -> 1
                value is Number -> 2
                value == null -> 3
                else -> 4
            }
            """.trimIndent()
        )

        assertThat(findings).singleElement()
            .hasMessage(
                "Rewrite this as `when (value)` to enable JVM invokedynamic type-switch compilation for its " +
                    "String, Number, null checks."
            )
    }

    @Test
    fun `reports type checks joined by or`() {
        val findings = lint(
            """
            fun classify(value: Any) = when {
                value is String || value is StringBuilder -> 1
                value is Number -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `ignores an eligible subject when`() {
        val findings = lint(
            """
            fun classify(value: Any?) = when (value) {
                is String -> 1
                is Number -> 2
                null -> 3
                else -> 4
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores checks against different subjects`() {
        val findings = lint(
            """
            fun classify(first: Any, second: Any) = when {
                first is String -> 1
                second is Number -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores a negated type check`() {
        val findings = lint(
            """
            fun classify(value: Any) = when {
                value !is String -> 1
                value is Number -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores mutable collection and function type checks`() {
        val findings = lint(
            """
            fun classify(value: Any) = when {
                value is MutableList<*> -> 1
                value is Function1<*, *> -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores conditions with additional predicates`() {
        val findings = lint(
            """
            fun classify(value: Any, enabled: Boolean) = when {
                value is String && enabled -> 1
                value is Number -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores a subject expression that could be evaluated more than once`() {
        val findings = lint(
            """
            fun next(): Any = Any()

            fun classify() = when {
                next() is String -> 1
                next() is Number -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores a property with a changing getter`() {
        val findings = lint(
            """
            var reads = 0
            val value: Any
                get() = if (reads++ == 0) 0 else "later"

            fun classify() = when {
                value is String -> 1
                value is Number -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores a delegated local val`() {
        val findings = lint(
            """
            import kotlin.reflect.KProperty

            class ChangingDelegate {
                private var reads = 0

                operator fun getValue(thisRef: Any?, property: KProperty<*>): Any =
                    if (reads++ == 0) 0 else "later"
            }

            fun classify(): Int {
                val value by ChangingDelegate()
                return when {
                    value is String -> 1
                    value is Number -> 2
                    else -> 3
                }
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports checks against a non-delegated local val`() {
        val findings = lint(
            """
            fun classify(input: Any): Int {
                val value = input
                return when {
                    value is String -> 1
                    value is Number -> 2
                    else -> 3
                }
            }
            """.trimIndent()
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `ignores mutable collection and function type aliases`() {
        val findings = lint(
            """
            typealias Items<T> = MutableList<T>
            typealias Handler = (String) -> Unit

            fun classifyItems(value: Any) = when {
                value is Items<*> -> 1
                value is String -> 2
                else -> 3
            }

            fun classifyHandler(value: Any) = when {
                value is Handler -> 1
                value is String -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores an imported mutable collection alias`() {
        val findings = lint(
            """
            import kotlin.collections.MutableList as Items

            fun classify(value: Any) = when {
                value is Items<*> -> 1
                value is String -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    @Test
    fun `ignores remaining mutable collection interfaces`() {
        val findings = lint(
            """
            fun classifyIterator(value: Any) = when {
                value is MutableIterator<*> -> 1
                value is String -> 2
                else -> 3
            }

            fun classifyListIterator(value: Any) = when {
                value is MutableListIterator<*> -> 1
                value is String -> 2
                else -> 3
            }

            fun classifyEntry(value: Any) = when {
                value is MutableMap.MutableEntry<*, *> -> 1
                value is String -> 2
                else -> 3
            }
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }

    private fun lint(source: String) = rule.lintWithContext(env, source.trimIndent())
}
