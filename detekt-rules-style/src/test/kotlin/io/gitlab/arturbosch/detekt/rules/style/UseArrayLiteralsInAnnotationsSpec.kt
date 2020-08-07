package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseArrayLiteralsInAnnotationsSpec : Spek({

    val subject by memoized { UseArrayLiteralsInAnnotations() }

    describe("suggests replacing arrayOf with [] syntax") {

        it("finds an arrayOf usage") {
            val findings = subject.compileAndLint("""
            annotation class Test(val values: Array<String>)
            @Test(arrayOf("value"))
            fun test() = Unit
        """.trimIndent())

            assertThat(findings).hasSize(1)
        }

        it("expects [] syntax") {
            val findings = subject.compileAndLint("""
            annotation class Test(val values: Array<String>)
            @Test(["value"])
            fun test() = Unit
        """.trimIndent())

            assertThat(findings).isEmpty()
        }
    }
})
