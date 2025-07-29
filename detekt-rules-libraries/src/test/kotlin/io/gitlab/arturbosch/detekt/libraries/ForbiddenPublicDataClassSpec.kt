package io.gitlab.arturbosch.detekt.libraries

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ForbiddenPublicDataClassSpec {
    val subject = ForbiddenPublicDataClass(Config.empty)

    @Test
    fun `public data class should fail`() {
        val code = """
            data class C(val a: String)
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `private data class should pass`() {
        val code = """
            private data class C(val a: String)
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `internal data class should pass`() {
        val code = """
            internal data class C(val a: String)
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `public class should pass`() {
        val code = """
            class C(val a: String)
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `private data class inside a public class should pass`() {
        val code = """
            class C {
                private data class D(val a: String)
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `public data class inside a public class should fail`() {
        val code = """
            class C {
                data class D(val a: String)
            }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `protected data class inside a public class should fail`() {
        val code = """
            open class C {
                protected data class D(val a: String)
            }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `public data class inside an internal class should pass`() {
        val code = """
            internal class C {
                data class D(val a: String)
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `public data class inside an internal package should pass`() {
        val code = """
            package com.example.internal
            
            data class C(val a: String)
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `public data class inside an internal subpackage should pass`() {
        val code = """
            package com.example.internal.other
            
            data class C(val a: String)
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `public data class inside an internalise package should fail`() {
        val code = """
            package com.example.internalise
            
            data class C(val a: String)
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `public data class inside a random package should fail`() {
        val code = """
            package com.random
            
            data class C(val a: String)
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `public data class inside an ignored package should pass`() {
        val code = """
            package com.example
            
            data class C(val a: String)
        """.trimIndent()

        val config = TestConfig("ignorePackages" to listOf("*.hello", "com.example"))
        assertThat(ForbiddenPublicDataClass(config).lint(code)).isEmpty()
    }
}
