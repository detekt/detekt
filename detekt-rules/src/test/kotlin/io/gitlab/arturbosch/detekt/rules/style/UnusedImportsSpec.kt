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
	}

	given("somee import statements with KDoc") {

		it("does not report imports in KDoc") {
			val code = """
				package com.acme

				import com.acme.cathedral.TheDome   // here
				import com.acme.bazar.SomeException // and here

				/**
				 * Do something.
				 * @throws [SomeException] when ...
				 * @see [TheDome.someMethod]
				 */
				fun doSomething() {
					TODO()
				}"""

			assertThat(subject.lint(code)).isEmpty()
		}
	}
})
