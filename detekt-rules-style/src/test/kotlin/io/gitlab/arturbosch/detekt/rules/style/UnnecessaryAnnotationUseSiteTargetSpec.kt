package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryAnnotationUseSiteTargetSpec : Spek({

    describe("UnnecessaryAnnotationUseSiteTarget rule") {

        it("Unnecessary @param: in a property constructor") {
            val code = """
                class C(@param:Asdf private val foo: String)

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).hasTextLocations("param:")
        }

        it("Unnecessary @param: in a constructor") {
            val code = """
                class C(@param:Asdf foo: String)

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).hasTextLocations("param:")
        }

        it("Necessary @get:") {
            val code = """
                class C(@get:Asdf private val foo: String)

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).isEmpty()
        }

        it("Necessary @property:") {
            val code = """
                class C(@property:Asdf private val foo: String)

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).isEmpty()
        }

        it("Unnecessary @property:") {
            val code = """
                class C {
                    @property:Asdf private val foo: String = "bar"
                }

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).hasTextLocations("property:")
        }

        it("Unnecessary @property: at a top level property") {
            val code = """
                @property:Asdf private val foo: String = "bar"

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).hasTextLocations("property:")
        }
    }
})
