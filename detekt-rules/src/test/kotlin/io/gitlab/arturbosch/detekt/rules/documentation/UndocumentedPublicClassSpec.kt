package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UndocumentedPublicClassSpec : Spek({
    val subject by memoized { UndocumentedPublicClass() }

    val inner = """
            /** Some doc */
            class TestInner {
                inner class Inner
            }"""

    val innerObject = """
            /** Some doc */
            class TestInner {
                object Inner
            }"""

    val innerInterface = """
            /** Some doc */
            class TestInner {
                interface Something
            }"""

    val nested = """
            /** Some doc */
            class TestNested {
                class Nested
            }"""

    val nestedPublic = """
            /** Some doc */
            class TestNested {
                public class Nested
            }
            """

    val nestedPrivate = """
            /** Some doc */
            class TestNested {
                private class Nested
            }
            """

    val privateClass = "private class TestNested {}"
    val internalClass = "internal class TestNested {}"

    describe("UndocumentedPublicClass rule") {

        it("should report inner classes by default") {
            assertThat(subject.compileAndLint(inner)).hasSize(1)
        }

        it("should report inner object by default") {
            assertThat(subject.compileAndLint(innerObject)).hasSize(1)
        }

        it("should report inner interfaces by default") {
            assertThat(subject.compileAndLint(innerInterface)).hasSize(1)
        }

        it("should report nested classes by default") {
            assertThat(subject.compileAndLint(nested)).hasSize(1)
        }

        it("should report explicit public nested classes by default") {
            assertThat(subject.compileAndLint(nestedPublic)).hasSize(1)
        }

        it("should not report internal classes") {
            assertThat(subject.compileAndLint(internalClass)).isEmpty()
        }

        it("should not report private classes") {
            assertThat(subject.compileAndLint(privateClass)).isEmpty()
        }

        it("should not report nested private classes") {
            assertThat(subject.compileAndLint(nestedPrivate)).isEmpty()
        }

        it("should not report inner classes when turned off") {
            val findings = UndocumentedPublicClass(TestConfig(mapOf(UndocumentedPublicClass.SEARCH_IN_INNER_CLASS to "false"))).compileAndLint(inner)
            assertThat(findings).isEmpty()
        }

        it("should not report inner objects when turned off") {
            val findings = UndocumentedPublicClass(TestConfig(mapOf(UndocumentedPublicClass.SEARCH_IN_INNER_OBJECT to "false"))).compileAndLint(innerObject)
            assertThat(findings).isEmpty()
        }

        it("should not report inner interfaces when turned off") {
            val findings = UndocumentedPublicClass(TestConfig(mapOf(UndocumentedPublicClass.SEARCH_IN_INNER_INTERFACE to "false"))).compileAndLint(innerInterface)
            assertThat(findings).isEmpty()
        }

        it("should not report nested classes when turned off") {
            val findings = UndocumentedPublicClass(TestConfig(mapOf(UndocumentedPublicClass.SEARCH_IN_NESTED_CLASS to "false"))).compileAndLint(nested)
            assertThat(findings).isEmpty()
        }

        it("should report missing doc over object declaration") {
            assertThat(subject.compileAndLint("object o")).hasSize(1)
        }

        it("should not report non-public nested classes") {
            val code = """
            internal class Outer {
                class Nested
                inner class Inner
            }
        """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not report non-public nested interfaces") {
            val code = """
            internal class Outer {
                interface Inner
            }
        """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not report non-public nested objects") {
            val code = """
            internal class Outer {
                object Inner
            }
        """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not report for documented public object") {
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
        """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not report for anonymous objects") {
            val code = """
            fun main(args: Array<String>) {
                val value = object : Iterator<Int> {
                    override fun hasNext() = true
                    override fun next() = 1
                }
            }
        """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should report for enum classes") {
            val code = """
            enum class Enum {
                CONSTANT
            }
        """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should not report for enum constants") {
            val code = """
            /** Some doc */
            enum class Enum {
                CONSTANT
            }
        """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
