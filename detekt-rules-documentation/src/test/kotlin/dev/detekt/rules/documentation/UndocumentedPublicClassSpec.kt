package dev.detekt.rules.documentation

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val SEARCH_IN_NESTED_CLASS = "searchInNestedClass"
private const val SEARCH_IN_INNER_CLASS = "searchInInnerClass"
private const val SEARCH_IN_INNER_OBJECT = "searchInInnerObject"
private const val SEARCH_IN_INNER_INTERFACE = "searchInInnerInterface"
private const val SEARCH_IN_PROTECTED_CLASS = "searchInProtectedClass"
private const val IGNORE_DEFAULT_COMPANION_OBJECT = "ignoreDefaultCompanionObject"

class UndocumentedPublicClassSpec {
    val subject = UndocumentedPublicClass(Config.empty)

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
        assertThat(subject.lint(inner)).hasSize(1)
    }

    @Test
    fun `should report inner object by default`() {
        assertThat(subject.lint(innerObject)).hasSize(1)
    }

    @Test
    fun `should report inner interfaces by default`() {
        assertThat(subject.lint(innerInterface)).hasSize(1)
    }

    @Test
    fun `should report nested classes by default`() {
        assertThat(subject.lint(nested)).hasSize(1)
    }

    @Test
    fun `should report explicit public nested classes by default`() {
        assertThat(subject.lint(nestedPublic)).hasSize(1)
    }

    @Test
    fun `should not report internal classes`() {
        assertThat(subject.lint(internalClass)).isEmpty()
    }

    @Test
    fun `should not report private classes`() {
        assertThat(subject.lint(privateClass)).isEmpty()
    }

    @Test
    fun `should not report nested private classes`() {
        assertThat(subject.lint(nestedPrivate)).isEmpty()
    }

    @Test
    fun `should not report inner classes when turned off`() {
        val findings =
            UndocumentedPublicClass(TestConfig(SEARCH_IN_INNER_CLASS to "false")).lint(inner)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report inner objects when turned off`() {
        val findings =
            UndocumentedPublicClass(TestConfig(SEARCH_IN_INNER_OBJECT to "false")).lint(innerObject)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report inner interfaces when turned off`() {
        val findings =
            UndocumentedPublicClass(TestConfig(SEARCH_IN_INNER_INTERFACE to "false")).lint(
                innerInterface
            )
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report nested classes when turned off`() {
        val findings =
            UndocumentedPublicClass(TestConfig(SEARCH_IN_NESTED_CLASS to "false")).lint(nested)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report missing doc over object declaration`() {
        assertThat(subject.lint("object o")).hasSize(1)
    }

    @Test
    fun `should not report non-public nested classes`() {
        val code = """
            internal class Outer {
                class Nested
                inner class Inner
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `should not report non-public nested interfaces`() {
        val code = """
            internal class Outer {
                interface Inner
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `should not report non-public nested objects`() {
        val code = """
            internal class Outer {
                object Inner
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
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
        assertThat(subject.lint(code)).isEmpty()
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
        assertThat(subject.lint(code)).isEmpty()
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
        assertThat(subject.lint(code)).isEmpty()
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
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report protected class by default`() {
        val code = """
            /**
             * Sample KDoc for parent class.
             */
            class Test {
                protected class ProtectedClass
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports protected class if configured`() {
        val code = """
            /**
             * Sample KDoc for parent class.
             */
            class Test {
                protected class ProtectedClass
            }
        """.trimIndent()
        val subject = UndocumentedPublicClass(TestConfig(SEARCH_IN_PROTECTED_CLASS to "true"))
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `should report in public companion class - #7217`() {
        val code = """
            public object PublicObject

            public class PublicClass {
                public companion object
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(3)
    }

    @Test
    fun `should not report in public default companion class with content if ignored`() {
        val code = """
            /** Some doc */
            public class PublicWithContent {
                public companion object {
                    public val x: String = ""
                }
            }

            /** Some doc */
            public class DefaultPublicWithContent {
                companion object {
                    public val x: String = ""
                }
            }

            /** Some doc */
            public class PublicEmpty {
                public companion object
            }

            /** Some doc */
            public class DefaultPublicEmpty {
                companion object
            }

        """.trimIndent()
        assertThat(
            UndocumentedPublicClass(
                TestConfig(IGNORE_DEFAULT_COMPANION_OBJECT to "true")
            ).lint(code)
        ).isEmpty()
    }

    // really strange edge-case, likely from generated code. Because it's "not default," it is reported
    @Test
    fun `should report in public default companion object named Companion if ignored`() {
        val code = """
            /** Some doc */
            public class ContentClass {            
                public companion object Companion {
                    public val x: String = ""
                }
            }

            /** Some doc */
            public class EmptyPublic {
                public companion object Companion
            }

            /** Some doc */
            public class EmptyDefaultPublic {
                companion object Companion
            }

            /** Some doc */
            public class ContentDefaultPublic {
                companion object Companion{
                    public val x: String = ""
                }
            }
        """.trimIndent()
        assertThat(
            UndocumentedPublicClass(
                TestConfig(IGNORE_DEFAULT_COMPANION_OBJECT to "true")
            ).lint(code)
        ).hasSize(4)
    }

    @Test
    fun `should report for public named companion classes if ignore disabled`() {
        val code = """
            /** Some doc */
            public class ContentClass {
                public companion object Content {
                    public val x: String = ""
                }
            }

            /** Some doc */
            public class BracesClass {
                public companion object Braces {}
            }

            /** Some doc */
            public class EmptyClass {
                public companion object Empty
            }
        """.trimIndent()
        assertThat(
            UndocumentedPublicClass(
                TestConfig(IGNORE_DEFAULT_COMPANION_OBJECT to "true")
            ).lint(code)
        ).hasSize(3)
    }

    @Test
    fun `should not report in private or internal companion class - #7217`() {
        val code = """
            public class PublicObject {
                internal companion object
            }

            public class PublicClass {
                private companion object
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(2)
    }

    @Nested
    inner class `enum classes` {
        @Test
        fun `does not report documented enum class in public enum`() {
            val code = """
                /**
                * This is PublicEnum
                */
                enum class PublicEnum {
                    Foo,
                    Bar,
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does report undocumented enum class in public enum`() {
            val code = """
                enum class PublicEnum {
                    Foo,
                    Bar,
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report undocumented enum entries in private and internal enum`() {
            val code = """
                private enum class PrivateEnum {
                    Foo,
                    Bar,
                }

                internal enum class InternalEnum {
                    Foo,
                    Bar,
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
