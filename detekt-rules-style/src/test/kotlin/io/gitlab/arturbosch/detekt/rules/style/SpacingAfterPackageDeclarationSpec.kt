package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileContentForTest
import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpacingAfterPackageDeclarationSpec {
    val subject = SpacingAfterPackageDeclaration(Config.empty)

    @Test
    fun `has no blank lines violation`() {
        val code = "package test\n\nimport a.b\n\nclass A {}"
        assertThat(subject.lint(code, compile = false)).isEmpty()
    }

    @Test
    fun `has a package and import declaration`() {
        val code = "package test\n\nimport a.b"
        assertThat(subject.lint(code, compile = false)).isEmpty()
    }

    @Test
    fun `has no import declaration`() {
        val code = "package test\n\nclass A {}"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `has no package declaration`() {
        val code = "import a.b\n\nclass A {}"
        assertThat(subject.lint(code, compile = false)).isEmpty()
    }

    @Test
    fun `has no package and import declaration`() {
        val code = "class A {}"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `has a comment declaration`() {
        val code = "import a.b\n\n// a comment"
        assertThat(subject.lint(code, compile = false)).isEmpty()
    }

    @Test
    fun `is an empty kt file`() {
        assertThat(subject.lint("")).isEmpty()
    }

    @Nested
    inner class `Kotlin scripts` {

        @Test
        fun `has no package declaration in script`() {
            val code = "import a.b\n\nprint(1)"
            val ktsFile = compileContentForTest(code, "Test.kts")
            assertThat(subject.lint(ktsFile)).isEmpty()
        }

        @Test
        fun `has no package and import declaration in script`() {
            val code = "print(1)"
            val ktsFile = compileContentForTest(code, "Test.kts")
            assertThat(subject.lint(ktsFile)).isEmpty()
        }

        @Test
        fun `has import declarations separated by new line in script`() {
            val code = "import a.b\n\nimport a.c\n\nprint(1)"
            val ktsFile = compileContentForTest(code, "Test.kts")
            assertThat(subject.lint(ktsFile)).isEmpty()
        }
    }

    @Test
    fun `has code on new line`() {
        val code = "package test\nimport a.b\nclass A {}"
        assertThat(subject.lint(code, compile = false)).hasSize(2)
    }

    @Test
    fun `has code with spaces`() {
        val code = "package test; import a.b; class A {}"
        assertThat(subject.lint(code, compile = false)).hasSize(2)
    }

    @Test
    fun `has too many blank lines`() {
        val code = "package test\n\n\nimport a.b\n\n\nclass A {}"
        assertThat(subject.lint(code, compile = false)).hasSize(2)
    }

    @Test
    fun `has package declarations in same line`() {
        val code = "package test;import a.b;class A {}"
        assertThat(subject.lint(code, compile = false)).hasSize(2)
    }

    @Test
    fun `has multiple imports in file`() {
        val code = """
            package com.my
            
            import kotlin.collections.List
            import kotlin.collections.Set
            
            class A { }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `has no class`() {
        val code = """
            package com.my.has.no.clazz
            
            import kotlin.collections.List
            import kotlin.collections.Set
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
