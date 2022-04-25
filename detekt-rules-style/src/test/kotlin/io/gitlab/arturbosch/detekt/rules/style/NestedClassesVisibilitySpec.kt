package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NestedClassesVisibilitySpec {
    val subject = NestedClassesVisibility()

    @Test
    fun `reports explicit public visibility in nested objects, classes and interfaces`() {
        val code = """
            internal class Outer {
                public interface A
                public object B
                public class C
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(3)
    }

    @Test
    fun `reports explicit public visibility in nested classes inside an enum`() {
        val code = """
            internal enum class Outer {
                A;
                public class C
            }
        """
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report nested internal classes and interfaces`() {
        val code = """
            internal class Outer {
                 class A
                 internal class B
                 enum class E { One }
                 internal interface I
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report nested private classes`() {
        val code = """
            internal class Outer {
                private class A
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report nested public enums`() {
        val code = """
            internal class Outer {
                public enum class E { E1; }
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report companion object that is explicitly public`() {
        val code = """
            internal class Outer {
                public companion object C
            } 
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report companion object`() {
        val code = """
            internal class Outer {
                companion object C
            } 
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report nested classes inside a private class`() {
        val code = """
            private class Outer {
                 class A
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report nested internal classes inside an interface`() {
        val code = """
            internal interface Outer {
                 class A
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report nested classes with a nesting depth higher than 1`() {
        val code = """
            internal class Outer {
                class C1 {
                    public class C2
                }
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
