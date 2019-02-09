package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Shyiko
 * @author Artur Bosch
 * @author Mauin
 * @author schalkms
 */
class UnusedImportsSpec : SubjectSpek<UnusedImports>({
    subject { UnusedImports(Config.empty) }

    given("some import statements") {

        it("does not report infix operators") {
            assertThat(subject.lint("""
            	import tasks.success

            	fun main() {
					task {
					} success {
					}
            	}"""
            )).isEmpty()
        }

        it("does not report imports in documentation") {
            assertThat(subject.lint("""
           		import tasks.success
           		import tasks.failure
           		import tasks.undefined

           		/**
           		*  Reference to [failure]
           		*/
           		class Test{
           		  /** Reference to [undefined]*/
           		  fun main() {
           		    task {
           		    } success {
           		    }
           		  }
           		}
           		"""
            )).isEmpty()
        }

        it("should ignore import for link") {
            val lint = subject.lint("""
				import tasks.success
				import tasks.failure
				import tasks.undefined

				/**
				* Reference [undefined][failure]
				*/
				fun main() {
				task {
				} success {
				}
				}
            """
            )
            with(lint) {
                assertThat(this).hasSize(1)
                assertThat(this[0].entity.signature).endsWith("import tasks.undefined")
            }
        }

        it("reports imports from the current package") {
            val lint = subject.lint("""
				package test
				import test.SomeClass

				val a: SomeClass? = null
			"""
            )
            with(lint) {
                assertThat(this).hasSize(1)
                assertThat(this[0].entity.signature).endsWith("import test.SomeClass")
            }
        }

        it("does not report KDoc references with method calls") {
            val code = """
				package com.example

				import android.text.TextWatcher

				class Test {
					/**
					 * [TextWatcher.beforeTextChanged]
					 */
					fun test() {
						TODO()
					}
				}"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports imports with different cases") {
            val lint = subject.lint("""
            	import p.a
            	import p.B6 // positive
            	import p.B as B12 // positive
            	import p2.B as B2
            	import p.C
            	import escaped.`when`
            	import escaped.`foo` // positive
            	import p.D

            	/** reference to [D] */
            	fun main() {
            	    println(a())
            	    C.call()
            	    fn(B2.NAME)
            	    `when`()
            	}"""
            )
            with(lint) {
                assertThat(this).hasSize(3)
                assertThat(this[0].entity.signature).contains("import p.B6")
                assertThat(this[1].entity.signature).contains("import p.B as B12")
                assertThat(this[2].entity.signature).contains("import escaped.`foo`")
            }
        }

        it("does not report imports in same package when inner") {
            val lint = subject.lint("""
            	package test

				import test.Outer.Inner

				class Outer : Something<Inner>() {
					class Inner { }
				}"""
            )
            with(lint) {
                assertThat(this).isEmpty()
            }
        }
    }

    given("some import statements referenced by KDoc @see") {

        it("does not report see annotation linking to class") {
            val code = """
				import tasks.success

				/**
				 * Do something.
				 * @see success
				 */
				fun doSomething()"""

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report see annotation linking to class with description") {
            val code = """
				import tasks.success

				/**
				 * Do something.
				 * @see success something
				 */
				fun doSomething()"""

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports see annotation that does not link to class") {
            val code = """
				import tasks.success

				/**
				 * Do something.
				 * @see something
				 */
				fun doSomething()"""

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports see annotation that links after description") {
            val code = """
				import tasks.success

				/**
				 * Do something.
				 * @see something success
				 */
				fun doSomething()"""

            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    given("some import statements with KDoc") {

        it("does not report imports in KDoc") {
            val code = """
				import tasks.success   // here
				import tasks.undefined // and here

				/**
				 * Do something.
				 * @throws [success] when ...
				 * @see [undefined]
				 */
				fun doSomething()"""

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    given("imports with aliases") {

        it("should not report import as unused because the alias is used") {
            val code = """
				import test.forEach as foreach
				fun foo() = listOf().iterator().foreach {}
			"""
            assertThat(subject.lint(code)).isEmpty()
        }

        it("should not report used alias even when import is from same package") {
            val code = """
				package com.example

				import com.example.foo as myFoo // from same package but with alias, check alias usage
				import com.example.other.foo as otherFoo // not from package with used alias

				fun f() : Boolean {
					return myFoo() == otherFoo()
				}
			""".trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
