package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PackageNamingSpec {

    @Nested
    inner class `PackageNaming rule` {

        @Test
        fun `should ignore the issue by alias suppression`() {
            assertThat(
                PackageNaming().compileAndLint(
                    """
                    @file:Suppress("PackageDirectoryMismatch")
                    package FOO.BAR
                """
                )
            ).isEmpty()
        }

        @Test
        fun `should find a uppercase package name`() {
            assertThat(PackageNaming().compileAndLint("package FOO.BAR")).hasSize(1)
        }

        @Test
        fun `should find a upper camel case package name`() {
            assertThat(PackageNaming().compileAndLint("package Foo.Bar")).hasSize(1)
        }

        @Test
        fun `should find a camel case package name`() {
            assertThat(PackageNaming().compileAndLint("package fOO.bAR")).hasSize(1)
        }

        @Test
        fun `should check an valid package name`() {
            assertThat(PackageNaming().compileAndLint("package foo.bar")).isEmpty()
        }
    }
}
