package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PackageNamingSpec {

    @Test
    fun `should use custom name for package`() {
        val rule = PackageNaming(TestConfig(PackageNaming.PACKAGE_PATTERN to "^(package_1)$"))
        assertThat(rule.lint("package package_1", compile = false)).isEmpty()
    }

    @Test
    fun `should find a uppercase package name`() {
        assertThat(PackageNaming(Config.empty).lint("package FOO.BAR", compile = false)).hasSize(1)
    }

    @Test
    fun `should find a upper camel case package name`() {
        assertThat(PackageNaming(Config.empty).lint("package Foo.Bar", compile = false)).hasSize(1)
    }

    @Test
    fun `should find a camel case package name`() {
        assertThat(PackageNaming(Config.empty).lint("package fOO.bAR", compile = false)).hasSize(1)
    }

    @Test
    fun `should check an valid package name`() {
        assertThat(PackageNaming(Config.empty).lint("package foo.bar", compile = false)).isEmpty()
    }
}
