package dev.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PackageNamingSpec {

    @Test
    fun `should use custom name for package`() {
        val rule = PackageNaming(TestConfig(PackageNaming.PACKAGE_PATTERN to "^(package_1)$"))
        assertThat(rule.lint("package package_1")).isEmpty()
    }

    @Test
    fun `should find a uppercase package name`() {
        assertThat(PackageNaming(Config.empty).lint("package FOO.BAR")).hasSize(1)
    }

    @Test
    fun `should find a upper camel case package name`() {
        assertThat(PackageNaming(Config.empty).lint("package Foo.Bar")).hasSize(1)
    }

    @Test
    fun `should find a camel case package name`() {
        assertThat(PackageNaming(Config.empty).lint("package fOO.bAR")).hasSize(1)
    }

    @Test
    fun `should check an valid package name`() {
        assertThat(PackageNaming(Config.empty).lint("package foo.bar")).isEmpty()
    }
}
