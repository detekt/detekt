package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

private const val SEARCH_IN_NESTED_CLASS = "searchInNestedClass"
private const val SEARCH_IN_INNER_CLASS = "searchInInnerClass"
private const val SEARCH_IN_INNER_OBJECT = "searchInInnerObject"
private const val SEARCH_IN_INNER_INTERFACE = "searchInInnerInterface"
private const val SEARCH_IN_PROTECTED_CLASS = "searchInProtectedClass"

class UndocumentedPublicClassSpec {
    val subject = UndocumentedPublicClass()

    val inner = """
        /** Some doc */
        class TestInner {
            inner class Inner
        }
    """.trimIndent()

    val innerObject = """
        /** Some doc */
        class TestInner {
            object Inner
        }
    """.trimIndent()

    val innerInterface = """
        /** Some doc */
        class TestInner {
            interface Something
        }
    """.trimIndent()

    val nested = """
        /** Some doc */
        class TestNested {
            class Nested
        }
    """.trimIndent()

    val nestedPublic = """
        /** Some doc */
        class TestNested {
            public class Nested
        }
    """.trimIndent()

    val nestedPrivate = """
        /** Some doc */
        class TestNested {
            private class Nested
        }
    """.trimIndent()

    val privateClass = "private class TestNested {}"
    val internalClass = "internal class TestNested {}"

    @Test
    fun `should report inner classes by default`() {
        assertThat(subject.compileAndLint(inner)).hasSize(1)
    }

    @Test
    fun `should report inner object by default`() {
        assertThat(subject.compileAndLint(innerObject)).hasSize(1)
    }

    @Test
    fun `should report inner interfaces by default`() {
        assertThat(subject.compileAndLint(innerInterface)).hasSize(1)
    }

    @Test
    fun `should report nested classes by default`() {
        assertThat(subject.compileAndLint(nested)).hasSize(1)
    }

    @Test
    fun `should report explicit public nested classes by default`() {
        assertThat(subject.compileAndLint(nestedPublic)).hasSize(1)
    }

    @Test
    fun `should not report internal classes`() {
        assertThat(subject.compileAndLint(internalClass)).isEmpty()
    }

    @Test
    fun `should not report private classes`() {
        assertThat(subject.compileAndLint(privateClass)).isEmpty()
    }

    @Test
    fun `should not report nested private classes`() {
        assertThat(subject.compileAndLint(nestedPrivate)).isEmpty()
    }

    @Test
    fun `should not report inner classes when turned off`() {
        val findings =
            UndocumentedPublicClass(TestConfig(SEARCH_IN_INNER_CLASS to "false")).compileAndLint(inner)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report inner objects when turned off`() {
        val findings =
            UndocumentedPublicClass(TestConfig(SEARCH_IN_INNER_OBJECT to "false")).compileAndLint(innerObject)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report inner interfaces when turned off`() {
        val findings =
            UndocumentedPublicClass(TestConfig(SEARCH_IN_INNER_INTERFACE to "false")).compileAndLint(
                innerInterface
            )
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report nested classes when turned off`() {
        val findings =
            UndocumentedPublicClass(TestConfig(SEARCH_IN_NESTED_CLASS to "false")).compileAndLint(nested)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report missing doc over object declaration`() {
        assertThat(subject.compileAndLint("object o")).hasSize(1)
    }

    @Test
    fun `should not report non-public nested classes`() {
        val code = """
            internal class Outer {
                class Nested
                inner class Inner
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report non-public nested interfaces`() {
        val code = """
            internal class Outer {
                interface Inner
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report non-public nested objects`() {
        val code = """
            internal class Outer {
                object Inner
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report for documented public object`() {
        val code = """
            /**
             * Class docs not being recognized.
             */
            object Main {
                /**
                 * The entry point for the application.
                 *
                 * @param args The list of process arguments.
                 */
                @JvmStatic
                fun main(args: Array<String>) {
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report for anonymous objects`() {
        val code = """
            fun main(args: Array<String>) {
                val value = object : Iterator<Int> {
                    override fun hasNext() = true
                    override fun next() = 1
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should report for enum classes`() {
        val code = """
            enum class Enum {
                CONSTANT
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `should not report for enum constants`() {
        val code = """
            /** Some doc */
            enum class Enum {
                CONSTANT
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report for fun interfaces`() {
        val code = """
            /**
             * This interface is an example
             */
            fun interface Example {
                /**
                 * Trigger when done
                 */
                fun onComplete()
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should report for uncommented interface`() {
        val code = """
            fun interface Interface {
                /**
                * This method has a comment but
                * the parent interface does not
                */
                fun abstractMethod()
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report protected class by default`() {
        val code = """
            protected class Test {
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports protected class if configured`() {
        val code = """
            protected class Test {
            }
        """.trimIndent()
        val subject = UndocumentedPublicClass(TestConfig(SEARCH_IN_PROTECTED_CLASS to "true"))
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }
}
