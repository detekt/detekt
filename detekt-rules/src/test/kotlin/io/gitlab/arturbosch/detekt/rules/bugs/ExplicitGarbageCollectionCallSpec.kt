package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ExplicitGarbageCollectionCallSpec : Spek({
    val subject by memoized { ExplicitGarbageCollectionCall(Config.empty) }

    describe("ExplicitGarbageCollectionCall rule") {

        it("reports garbage collector calls") {
            val code = """
                fun f() {
                    System.gc()
                    Runtime.getRuntime().gc()
                    System.runFinalization()
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(3)
        }
    }
})
