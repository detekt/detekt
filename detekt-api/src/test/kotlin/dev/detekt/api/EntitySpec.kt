package dev.detekt.api

import dev.detekt.test.compileForTest
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
                .isEqualTo("C\$private fun memberFun(): Int")
        }

        @Test
        fun `includes full function header and filename for a top level function`() {
            val topLevelFunction = functions.first { it.name == "topLevelFun" }

            assertThat(Entity.atName(topLevelFunction).signature)
                .isEqualTo("fun topLevelFun(number: Int)")
        }

        @Test
        fun `toString gives all details`() {
            val memberFunction = functions.first { it.name == "memberFun" }

            assertThat(Entity.atName(memberFunction).toString())
                .isEqualTo(
                    "Entity(signature=C\$private fun memberFun(): Int, " +
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
            assertThat(Entity.atName(clazz).signature).isEqualTo("C : Any")
        }

        @Test
        fun `toString gives all details`() {
            assertThat(Entity.atName(clazz).toString())
                .isEqualTo(
                    "Entity(signature=C : Any, " +
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
            val expectedResult = "test.EntitySpecFixture.kt"

            assertThat(Entity.from(code).signature).isEqualTo(expectedResult)
            assertThat(Entity.atPackageOrFirstDecl(code).signature).isEqualTo(expectedResult)
        }

        @Test
        fun `toString gives all details`() {
            assertThat(Entity.from(code).toString())
                .isEqualTo(
                    "Entity(signature=test.EntitySpecFixture.kt, " +
                        "location=Location(source=1:1, endSource=9:1, text=0:109, " +
                        "path=$path), " +
                        "ktElement=KtFile: EntitySpecFixture.kt)"
                )
        }
    }
}
