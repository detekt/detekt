package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MapGetWithNotNullAssertSpec : Spek({
    val subject by memoized { MapGetWithNotNullAssert(Config.empty) }

    lateinit var environment: KotlinCoreEnvironment

    beforeEachTest {
        environment = KtTestCompiler.createEnvironment()
    }

    describe("check for MapGetWithNotNullAssert") {

        it("reports map[] with not null assertion") {
            val code = """
				fun f() {
                    val map = emptyMap<Any, Any>()
					val value = map["key"]!!
				}"""
            assertThat(subject.compileAndLintWithContext(environment, code)).hasSize(1)
        }

        it("reports map.get() with not null assertion") {
            val code = """
				fun f() {
                    val map = emptyMap<Any, Any>()
					val value = map.get("key")!!
				}"""
            assertThat(subject.compileAndLintWithContext(environment, code)).hasSize(1)
        }
    }

})
