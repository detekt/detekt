package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PackageNamingSpec {

    @Test
    fun `should use custom name for package`() {
        val rule = PackageNaming(TestConfig(PackageNaming.PACKAGE_PATTERN to "^(package_1)$"))
        assertThat(rule.compileAndLint("package package_1")).isEmpty()
    }

    @Test
    fun `should ignore the issue by alias suppression - PackageName`() {
        assertThat(
            PackageNaming().compileAndLint(
                """
                    @file:Suppress("PackageName")
                    package FOO.BAR
                """.trimIndent()
            )
        ).isEmpty()
    }

    @Test
    fun `should ignore the issue by alias suppression - PackageDirectoryMismatch`() {
        assertThat(
            PackageNaming().compileAndLint(
                """
                    @file:Suppress("PackageDirectoryMismatch")
                    package FOO.BAR
                """.trimIndent()
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
