package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class LibraryEntitiesCannotBePublicTest : Spek({

    describe("Library class cannot be public") {
        it("should not report without explicit filters set") {
            assertThat(
                LibraryEntitiesCannotBePublic().compileAndLint("""
                    class A 
            """)).isEmpty()
        }

        val subject by memoized {
            LibraryEntitiesCannotBePublic(TestConfig(Config.INCLUDES_KEY to "*.kt"))
        }

        describe("positive cases") {
            it("should report a class") {
                assertThat(subject.compileAndLint("""
                    class A
                """)).hasSize(1)
            }

            it("should report a typealias") {
                assertThat(subject.compileAndLint("""
                    typealias A = List<String>
                """)).hasSize(1)
            }
        }

        describe("negative cases") {
            it("should not report a class") {
                assertThat(subject.compileAndLint("""
                    internal class A
                """)).hasSize(0)
            }

            it("should not report a typealias") {
                assertThat(subject.compileAndLint("""
                    internal typealias A = List<String>
                """)).hasSize(0)
            }
        }
    }

})
