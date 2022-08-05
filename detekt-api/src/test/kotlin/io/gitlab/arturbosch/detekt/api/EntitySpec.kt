package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class EntitySpec {

    private val path = Paths.get("/full/path/to/Test.kt")
    private val code = compileContentForTest(
        """
            package test

            class C : Any() {

                private fun memberFun(): Int = 5
            }

            fun topLevelFun(number: Int) = Unit
        """.trimIndent(),
        path.toString()
    )

    @Nested
    inner class `Named functions` {

        private val functions = code.collectDescendantsOfType<KtNamedFunction>()

        @Test
        fun `includes full function header, class name and filename`() {
            val memberFunction = functions.first { it.name == "memberFun" }

            assertThat(Entity.atName(memberFunction).signature)
                .isEqualTo("Test.kt\$C\$private fun memberFun(): Int")
        }

        @Test
        fun `includes full function header and filename for a top level function`() {
            val topLevelFunction = functions.first { it.name == "topLevelFun" }

            assertThat(Entity.atName(topLevelFunction).signature)
                .isEqualTo("Test.kt\$fun topLevelFun(number: Int)")
        }

        @Test
        fun `includes function name in entity compact`() {
            val memberFunction = functions.first { it.name == "memberFun" }

            assertThat(Entity.atName(memberFunction).compact())
                .isEqualTo("[memberFun] at $path:5:17")
        }
    }

    @Nested
    inner class `Classes` {
        private val clazz = requireNotNull(code.findDescendantOfType<KtClass>())

        @Test
        fun `includes full class signature`() {
            assertThat(Entity.atName(clazz).signature).isEqualTo("Test.kt\$C : Any")
        }

        @Test
        fun `includes class name in entity compact`() {
            assertThat(Entity.atName(clazz).compact()).isEqualTo("[C] at $path:3:7")
        }
    }

    @Nested
    inner class `Files` {

        @Test
        fun `includes package and file name in entity signature`() {
            assertThat(Entity.from(code).signature).isEqualTo("Test.kt\$test.Test.kt")
            assertThat(Entity.atPackageOrFirstDecl(code).signature).isEqualTo("Test.kt\$test.Test.kt")
        }

        @Test
        fun `includes file name in entity compact`() {
            val expectedResult = "[Test.kt] at $path:1:1"

            assertThat(Entity.from(code).compact()).isEqualTo(expectedResult)
            assertThat(Entity.atPackageOrFirstDecl(code).compact()).isEqualTo(expectedResult)
        }
    }
}
