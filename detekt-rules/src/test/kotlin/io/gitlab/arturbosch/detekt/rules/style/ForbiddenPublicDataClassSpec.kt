package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForbiddenPublicDataClassSpec : Spek({
    describe("ForbiddenPublicDataClass rule") {
        it("public data class should fail") {
            val code = """
                data class C(val a: String)                
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).hasSize(1)
        }

        it("private data class should pass") {
            val code = """
                private data class C(val a: String)                
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).isEmpty()
        }

        it("internal data class should pass") {
            val code = """
                internal data class C(val a: String)                
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).isEmpty()
        }

        it("public class should pass") {
            val code = """
                class C(val a: String) 
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).isEmpty()
        }

        it("private data class inside a public class should pass") {
            val code = """
                class C {
                    private data class D(val a: String)   
                }
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).isEmpty()
        }

        it("public data class inside a public class should fail") {
            val code = """
                class C {
                    data class D(val a: String)   
                }
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).hasSize(1)
        }

        it("protected data class inside a public class should fail") {
            val code = """
                open class C {
                    protected data class D(val a: String)   
                }
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).hasSize(1)
        }

        it("public data class inside an internal class should pass") {
            val code = """
                internal class C {
                    data class D(val a: String)   
                }
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).isEmpty()
        }

        it("public data class inside an internal package should pass") {
            val code = """
                package com.example.internal
                
                data class C(val a: String)                
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).isEmpty()
        }

        it("public data class inside an internal subpackage should pass") {
            val code = """
                package com.example.internal.other
                
                data class C(val a: String)                
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).isEmpty()
        }

        it("public data class inside an internalise package should fail") {
            val code = """
                package com.example.internalise
                
                data class C(val a: String)                
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).hasSize(1)
        }

        it("public data class inside a random should fail") {
            val code = """
                package com.example
                
                data class C(val a: String)                
            """

            assertThat(ForbiddenPublicDataClass().compileAndLint(code)).hasSize(1)
        }

        it("public data class inside an ignored package should pass") {
            val code = """
                package com.example
                
                data class C(val a: String)                
            """

            val config = TestConfig(mapOf("ignorePackages" to "*.hello,com.example"))
            assertThat(ForbiddenPublicDataClass(config).compileAndLint(code)).isEmpty()
        }
    }
})
