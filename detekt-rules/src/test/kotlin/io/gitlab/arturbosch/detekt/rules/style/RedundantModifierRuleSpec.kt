package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.*
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class RedundantModifierRuleSpec : SubjectSpek<RedundantVisibilityModifierRule>({
	subject { RedundantVisibilityModifierRule() }
	describe("check all cases") {
		it("check overridden function of abstract") {
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
	}
})


