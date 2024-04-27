package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.absolute

class EntitySpec {

    private val path = Path("src/test/resources/EntitySpecFixture.kt").absolute()
    private val code = compileForTest(path)

    @Nested
    inner class `Named functions` {

        private val functions = code.collectDescendantsOfType<KtNamedFunction>()

        @Test
        fun `includes full function header, class name and filename`() {
            val memberFunction = functions.first { it.name == "memberFun" }

            assertThat(Entity.atName(memberFunction).signature)
                .isEqualTo("EntitySpecFixture.kt\$C\$private fun memberFun(): Int")
        }

        @Test
        fun `includes full function header and filename for a top level function`() {
            val topLevelFunction = functions.first { it.name == "topLevelFun" }

            assertThat(Entity.atName(topLevelFunction).signature)
                .isEqualTo("EntitySpecFixture.kt\$fun topLevelFun(number: Int)")
        }

        @Test
        fun `includes function name in entity compact`() {
            val memberFunction = functions.first { it.name == "memberFun" }

            assertThat(Entity.atName(memberFunction).compact())
                .isEqualTo("[memberFun] at $path:5:17")
        }

        @Test
        fun `toString gives all details`() {
            val memberFunction = functions.first { it.name == "memberFun" }

            assertThat(Entity.atName(memberFunction).toString())
                .isEqualTo(
                    "Entity(name=memberFun, signature=EntitySpecFixture.kt\$C\$private fun memberFun(): Int, " +
                        "location=Location(source=5:17, endSource=5:26, text=49:58, " +
                        "path=$path), " +
                        "ktElement=FUN)"
                )
        }
    }

    @Nested
    inner class Classes {

        private val clazz = requireNotNull(code.findDescendantOfType<KtClass>())

        @Test
        fun `includes full class signature`() {
            assertThat(Entity.atName(clazz).signature).isEqualTo("EntitySpecFixture.kt\$C : Any")
        }

        @Test
        fun `includes class name in entity compact`() {
            assertThat(Entity.atName(clazz).compact()).isEqualTo("[C] at $path:3:7")
        }

        @Test
        fun `toString gives all details`() {
            assertThat(Entity.atName(clazz).toString())
                .isEqualTo(
                    "Entity(name=C, signature=EntitySpecFixture.kt\$C : Any, " +
                        "location=Location(source=3:7, endSource=3:8, text=20:21, " +
                        "path=$path), " +
                        "ktElement=CLASS)"
                )
        }
    }

    @Nested
    inner class Files {

        @Test
        fun `includes package and file name in entity signature`() {
            val expectedResult = "EntitySpecFixture.kt\$test.EntitySpecFixture.kt"

            assertThat(Entity.from(code).signature).isEqualTo(expectedResult)
            assertThat(Entity.atPackageOrFirstDecl(code).signature).isEqualTo(expectedResult)
        }

        @Test
        fun `includes file name in entity compact`() {
            val expectedResult = "[EntitySpecFixture.kt] at $path:1:1"

            assertThat(Entity.from(code).compact()).isEqualTo(expectedResult)
            assertThat(Entity.atPackageOrFirstDecl(code).compact()).isEqualTo(expectedResult)
        }

        @Test
        fun `toString gives all details`() {
            assertThat(Entity.from(code).toString())
                .isEqualTo(
                    "Entity(name=EntitySpecFixture.kt, signature=EntitySpecFixture.kt\$test.EntitySpecFixture.kt, " +
                        "location=Location(source=1:1, endSource=9:1, text=0:109, " +
                        "path=$path), " +
                        "ktElement=KtFile: EntitySpecFixture.kt)"
                )
        }
    }
}
