package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RedundantVisibilityModifierRuleSpec : Spek({
    val subject by memoized { RedundantVisibilityModifierRule() }
    describe("check all cases") {
        it("check overridden function of abstract class w/ public modifier") {
            val code = """
				abstract class A {
					abstract protected fun A()
				}

				class Test : A() {
					override public fun A() {}
				}
			"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("check overridden function of abstract class w/o public modifier") {
            val code = """
				abstract class A {
					abstract protected fun A()
				}

				class Test : A() {
					override fun A() {}
				}
			"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("check overridden function of interface") {
            val code = """
				interface A {
					fun A()
				}

				class Test : A {
					override public fun A() {}
				}
			"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("check public function") {
            val code = """
				class Test{
					public fun A() {}
				}
			"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("check function w/o modifier") {
            val code = """
				class Test{
					fun A() {}
				}
			"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("check public class") {
            val code = """
				public class Test(){
					fun test(){}
				}
			"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("check interface w/ public modifier") {
            val code = """
				public interface Test{
					public fun test()
				}
			"""
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("check field w/ public modifier") {
            val code = """
				class Test{
					public val str : String = "test"
				}
			"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("check field w/o public modifier") {
            val code = """
				class Test{
					val str : String = "test"
				}
			"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("check overridden field w/o public modifier ") {
            val code = """
				abstract class A {
					abstract val test: String
				}

				class B : A() {
					override val test: String = "valid"
				}
			"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("check overridden field w/ public modifier ") {
            val code = """
				abstract class A {
					abstract val test: String
				}

				class B : A() {
					override public val test: String = "valid"
				}
			"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }
})
