package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryInheritanceSpec : Spek({
    val subject by memoized { UnnecessaryInheritance(Config.empty) }

    describe("check inherit classes") {

        it("has unnecessary super type declarations") {
            val findings = subject.lint("""
                class A : Any()
                class B : Object()""")
            assertThat(findings).hasSize(2)
        }

        it("has no unnecessary super type declarations") {
            val findings = subject.lint("class C : An()")
            assertThat(findings).isEmpty()
        }
    }
})
