package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ImplicitDefaultLocaleSpec : Spek({
    val subject by memoized { ImplicitDefaultLocale(Config.empty) }

    describe("ImplicitDefault rule") {

        it("reports String.format call with template but without explicit locale") {
            val code = """
                fun x() {
                    String.format("%d", 1)
                }"""
            assertThat(subject.compileAndLint(code).size).isEqualTo(1)
        }

        it("does not report String.format call with explicit locale") {
            val code = """
                import java.util.Locale
                fun x() {
                    String.format(Locale.US, "%d", 1)
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
