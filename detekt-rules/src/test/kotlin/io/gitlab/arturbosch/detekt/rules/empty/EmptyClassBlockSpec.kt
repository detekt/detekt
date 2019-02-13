package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Egor Neliuba
 */
class EmptyClassBlockSpec : Spek({

    val subject by memoized { EmptyClassBlock(Config.empty) }

    describe("EmptyClassBlock rule") {

        it("flags the empty body") {
            val findings = subject.lint("class SomeClass {}")
            assertThat(findings).hasSize(1)
        }

        it("flags the object if it is of a non-anonymous class") {
            val findings = subject.lint("object SomeObject {}")
            assertThat(findings).hasSize(1)
        }

        it("does not flag the object if it is of an anonymous class") {
            val findings = subject.lint("object : SomeClass {}")
            assertThat(findings).isEmpty()
        }
    }
})
