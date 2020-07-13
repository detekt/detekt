package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryLetSpec : Spek({
    val subject by memoized { UnnecessaryLet(Config.empty) }

    describe("UnnecessaryLet rule") {
        it("reports unnecessary lets that can be changed to ordinary method call 1") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int = 1
                    a.let { it.plus(1) }
                    a.let { that -> that.plus(1) }
                }""")
            assertThat(findings).hasSize(2)
            assertThat(findings).allMatch { it.message == MESSAGE_OMIT_LET }
        }

        it("reports unnecessary lets that can be changed to ordinary method call 2") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    a?.let { it.plus(1) }
                    a?.let { that -> that.plus(1) }
                }""")
            assertThat(findings).hasSize(2)
            assertThat(findings).allMatch { it.message == MESSAGE_OMIT_LET }
        }

        it("reports unnecessary lets that can be changed to ordinary method call 3") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    a?.let { that -> that.plus(1) }?.let { it.plus(1) }
                }""")
            assertThat(findings).hasSize(2)
            assertThat(findings).allMatch { it.message == MESSAGE_OMIT_LET }
        }

        it("reports unnecessary lets that can be changed to ordinary method call 4") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int = 1
                    a.let { 1.plus(1) }
                    a.let { that -> 1.plus(1) }
                }""")
            assertThat(findings).hasSize(2)
            assertThat(findings).allMatch { it.message == MESSAGE_OMIT_LET }
        }

        it("reports unnecessary lets that can be changed to ordinary method call 5" ) {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int = 1
                    val x = a.let { 1.plus(1) }
                    val y = a.let { that -> 1.plus(1) }
                }""")
            assertThat(findings).hasSize(2)
            assertThat(findings).allMatch { it.message == MESSAGE_OMIT_LET }
        }

        it("reports unnecessary lets that can be replaced with an if") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    a?.let { 1.plus(1) }
                    a?.let { that -> 1.plus(1) }
                }""")
            assertThat(findings).hasSize(2)
            assertThat(findings).allMatch { it.message == MESSAGE_USE_IF }
        }

        it("reports unnecessary lets that can be changed to ordinary method call 7") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    a.let { print(it) }
                    a.let { that -> print(that) }
                }""")
            assertThat(findings).hasSize(2)
            assertThat(findings).allMatch { it.message == MESSAGE_OMIT_LET }
        }

        it("reports unnecessary lets that can be changed to ordinary method call 8") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    a?.let { it?.plus(1) }
                    a?.let { that -> that?.plus(1) }
                }""")
            assertThat(findings).hasSize(2)
            assertThat(findings).allMatch { it.message == MESSAGE_OMIT_LET }
        }

        it("reports use of let without the safe call operator when we use an argument") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val f: (Int?) -> Boolean = { true }
                    val a: Int? = null
                    a.let(f)
                }""")
            assertThat(findings).hasSize(1)
            assertThat(findings).allMatch { it.message == MESSAGE_OMIT_LET }
        }

        it("does not report lets used for function calls 1") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    a?.let { print(it) }
                    a?.let { that -> 1.plus(that) }
                }""")
            assertThat(findings).isEmpty()
        }

        it("does not report lets used for function calls 2") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    a?.let { that -> 1.plus(that) }?.let { print(it) }
                }""")
            assertThat(findings).isEmpty()
        }

        it("does not report \"can be replaced by if\" because you will need an else too") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    val x = a?.let { 1.plus(1) }
                    val y = a?.let { that -> 1.plus(1) }
                }""")
            assertThat(findings).isEmpty()
        }

        it("does not report use of let with the safe call operator when we use an argument") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val f: (Int?) -> Boolean = { true }
                    val a: Int? = null
                    a?.let(f)
                }""")
            assertThat(findings).hasSize(0)
        }

        it("does not report lets with lambda body containing more than one statement") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    val b: Int = 1
                    b.let {
                        it.plus(1)
                        it.plus(2)
                    }
                    a?.let {
                        it.plus(1)
                        it.plus(2)
                    }
                    b.let { that ->
                        that.plus(1)
                        that.plus(2)
                    }
                    a?.let { that ->
                        that.plus(1)
                        that.plus(2)
                    }
                    a?.let { that ->
                        1.plus(that)
                    }
                    ?.let {
                        it.plus(1)
                        it.plus(2)
                    }
                }""")
            assertThat(findings).isEmpty()
        }

        it("does not report lets where it is used multiple times") {
            val findings = subject.compileAndLint("""
                fun f() {
                    val a: Int? = null
                    val b: Int = 1
                    a?.let { it.plus(it) }
                    b.let { it.plus(it) }
                    a?.let { foo -> foo.plus(foo) }
                    b.let { foo -> foo.plus(foo) }
                }""")
            assertThat(findings).isEmpty()
        }
    }
})

private const val MESSAGE_OMIT_LET = "let expression can be omitted"
private const val MESSAGE_USE_IF = "let expression can be replaces with a simple if"
