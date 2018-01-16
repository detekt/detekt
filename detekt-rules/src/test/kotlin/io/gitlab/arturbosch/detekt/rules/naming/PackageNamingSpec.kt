package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class PackageNamingSpec : SubjectSpek<PackageNaming>({

	it("should find a uppercase package name") {
		assertThat(NamingRules().lint("package FOO.BAR")).hasSize(1)
	}

	it("should find a upper camel case package name") {
		assertThat(NamingRules().lint("package Foo.Bar")).hasSize(1)
	}

	it("should find a camel case package name") {
		assertThat(NamingRules().lint("package fOO.bAR")).hasSize(1)
	}

	it("should check an valid package name") {
		assertThat(NamingRules().lint("package foo.bar")).isEmpty()
	}
})
