package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object LambdaParameterNamingSpec : Spek({

    describe("lambda parameters") {
        it("Reports no supported parameter names") {
            val code = """
                val a: (String) -> Unit = { HELLO_THERE -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .hasSize(1)
                .hasTextLocations("HELLO_THERE")
        }

        it("Reports no supported parameter names when there are multiple") {
            val code = """
                val a: (String, Int) -> Unit = { HI, HELLO_THERE -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .hasSize(2)
                .hasTextLocations("HI", "HELLO_THERE")
        }

        it("Doesn't report a valid parameter") {
            val code = """
                val a: (String) -> Unit = { helloThere -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .isEmpty()
        }

        it("Doesn't report a valid parameter when define type") {
            val code = """
                val a = { helloThere: String -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .isEmpty()
        }

        it("Doesn't report _") {
            val code = """
                val a: (String) -> Unit = { _ -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .isEmpty()
        }

        it("Doesn't report by using implicit name") {
            val code = """
                val a: (String) -> Unit = { Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .isEmpty()
        }

        it("Doesn't report if there aren't parameters") {
            val code = """
                val a: () -> Unit = { Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .isEmpty()
        }

        it("Reports no supported destructuring parameter names") {
            val code = """
                data class Bar(val a: String)
                val a: (Bar) -> Unit = { (HELLO_THERE) -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .hasSize(1)
                .hasTextLocations("HELLO_THERE")
        }

        it("Reports no supported destructuring parameter names when there are multiple") {
            val code = """
                data class Bar(val a: String, val b: String)
                val a: (Bar) -> Unit = { (HI, HELLO_THERE) -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .hasSize(2)
                .hasTextLocations("HI", "HELLO_THERE")
        }

        it("Doesn't report valid destructuring parameters") {
            val code = """
                data class Bar(val a: String, val b: String)
                val a: (Bar) -> Unit = { (a, b) -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .isEmpty()
        }

        it("Doesn't report valid destructuring parameters when define type") {
            val code = """
                data class Bar(val a: String, val b: String)
                val a: (Bar) -> Unit = { (a: String, b) -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .isEmpty()
        }

        it("Doesn't report valid destructuring parameters using _") {
            val code = """
                data class Bar(val a: String, val b: String)
                val a: (Bar) -> Unit = { (_, b) -> Unit }
            """
            assertThat(LambdaParameterNaming().compileAndLint(code))
                .isEmpty()
        }
    }
})
