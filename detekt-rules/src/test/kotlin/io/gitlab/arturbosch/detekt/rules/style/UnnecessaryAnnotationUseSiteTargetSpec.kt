package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryAnnotationUseSiteTargetSpec : Spek({

    describe("UnnecessaryAnnotationUseSiteTarget rule") {

        context("Unnecessary @param: in a property constructor") {
            val code = """
                class C(@param:Asdf private val foo: String)

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).hasTextLocations("param:")
        }

        context("Unnecessary @param: in a constructor") {
            val code = """
                class C(@param:Asdf foo: String)

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).hasTextLocations("param:")
        }

        context("Necessary @get:") {
            val code = """
                class C(@get:Asdf private val foo: String)

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).isEmpty()
        }

        context("Necessary @property:") {
            val code = """
                class C(@property:Asdf private val foo: String)

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).isEmpty()
        }

        context("Unnecessary @property:") {
            val code = """
                class C {
                    @property:Asdf private val foo: String = "bar"
                }

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).hasTextLocations("property:")
        }

        context("Unnecessary @property: at a top level property") {
            val code = """
                @property:Asdf private val foo: String = "bar"

                annotation class Asdf
            """
            assertThat(UnnecessaryAnnotationUseSiteTarget().compileAndLint(code)).hasTextLocations("property:")
        }
    }
})
