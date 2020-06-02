package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ImplicitDefaultLocaleSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { ImplicitDefaultLocale(Config.empty) }

    describe("ImplicitDefault rule") {

        it("reports String.format call with template but without explicit locale") {
            val code = """
                fun x() {
                    String.format("%d", 1)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report String.format call with explicit locale") {
            val code = """
                import java.util.Locale
                fun x() {
                    String.format(Locale.US, "%d", 1)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports String.toUpperCase() call without explicit locale") {
            val code = """
                fun x() {
                    val s = "deadbeef"
                    s.toUpperCase()
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report String.toUpperCase() call with explicit locale") {
            val code = """
                import java.util.Locale
                fun x() {
                    val s = "deadbeef"
                    s.toUpperCase(Locale.US)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports String.toLowerCase() call without explicit locale") {
            val code = """
                fun x() {
                    val s = "deadbeef"
                    s.toLowerCase()
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report String.toLowerCase() call with explicit locale") {
            val code = """
                import java.util.Locale
                fun x() {
                    val s = "deadbeef"
                    s.toLowerCase(Locale.US)
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports String?.toUpperCase() call without explicit locale") {
            val code = """
                fun x() {
                    val s: String? = "deadbeef"
                    s?.toUpperCase()
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports String?.toLowerCase() call without explicit locale") {
            val code = """
                fun x() {
                    val s: String? = "deadbeef"
                    s?.toLowerCase()
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }
})
