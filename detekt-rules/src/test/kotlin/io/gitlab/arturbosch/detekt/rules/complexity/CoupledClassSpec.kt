package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CoupledClassSpec : Spek({

    val subject by memoized { CoupledClass(threshold = MAX_REFERENCED_CLASSES) }

    describe("CoupledClass rule positives") {

        context("constructor parameters") {
            val code = """
                class MyClass (p1: MySecondClass, p2: MyThirdClass)
                class MySecondClass
                class MyThirdClass 
            """

            it("reports coupled class") {
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports coupled class with custom config") {
                val rule = CoupledClass(threshold = MAX_REFERENCED_CLASSES)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }
    }
})

private const val MAX_REFERENCED_CLASSES = 2
