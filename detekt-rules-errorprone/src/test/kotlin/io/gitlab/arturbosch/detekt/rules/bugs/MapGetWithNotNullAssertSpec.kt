package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MapGetWithNotNullAssertSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { MapGetWithNotNullAssertionOperator(Config.empty) }

    describe("check for MapGetWithNotNullAssert") {

        it("reports map[] with not null assertion") {
            val code = """
                fun f() {
                    val map = emptyMap<Any, Any>()
                    val value = map["key"]!!
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("reports map.get() with not null assertion") {
            val code = """
                fun f() {
                    val map = emptyMap<Any, Any>()
                    val value = map.get("key")!!
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report map[] call without not-null assert") {
            val code = """
                fun f() {
                    val map = emptyMap<String, String>()
                    map["key"]
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report map.getValue() call") {
            val code = """
                fun f() {
                    val map = emptyMap<String, String>()
                    map.getValue("key")
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report map.getOrDefault() call") {
            val code = """
                fun f() {
                    val map = emptyMap<String, String>()
                    map.getOrDefault("key", "")
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report map.getOrElse() call") {
            val code = """
                fun f() {
                    val map = emptyMap<String, String>()
                    map.getOrElse("key", { "" })
                }"""
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
