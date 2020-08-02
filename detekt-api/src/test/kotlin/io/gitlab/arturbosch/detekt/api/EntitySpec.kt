package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class EntitySpec : Spek({

    describe("entity signatures") {

        val code by memoized(CachingMode.SCOPE) {
            compileContentForTest("""
            package test

            class C : Any() {

                private fun memberFun(): Int = 5
            }

            fun topLevelFun(number: Int) = Unit
        """.trimIndent(), Paths.get("/full/path/to/Test.kt").toString())
        }

        describe("functions") {

            val functions by memoized { code.collectDescendantsOfType<KtNamedFunction>() }

            it("includes full function header, class name and filename") {
                val memberFunction = functions.first { it.name == "memberFun" }

                assertThat(Entity.atName(memberFunction).signature)
                    .isEqualTo("Test.kt\$C\$private fun memberFun(): Int")
            }

            it("includes full function header and filename for a top level function") {
                val topLevelFunction = functions.first { it.name == "topLevelFun" }

                assertThat(Entity.atName(topLevelFunction).signature)
                    .isEqualTo("Test.kt\$fun topLevelFun(number: Int)")
            }
        }

        describe("classes") {

            it("includes full class signature") {
                val clazz = requireNotNull(code.findDescendantOfType<KtClass>())

                assertThat(Entity.atName(clazz).signature).isEqualTo("Test.kt\$C : Any")
            }
        }

        describe("files") {

            it("includes package and file name") {
                assertThat(Entity.from(code).signature).isEqualTo("Test.kt\$test.Test.kt")
                assertThat(Entity.atPackageOrFirstDecl(code).signature).isEqualTo("Test.kt\$test.Test.kt")
            }
        }
    }
})
