package dev.detekt.test.junit

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.compileContentForTest
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest(additionalJavaSourcePaths = ["java"])
class CompileWithSourcesTest(private val environment: KotlinEnvironmentContainer) {
    @Test
    fun `can compile snippet with test resources`() {
        val content = $$"""
            import com.example.dummy.Bar
            import com.example.dummy.Foo

            fun test(): Foo {
                val bar = Bar(123)
                return Foo("abc ${bar.value}")
            }
        """.trimIndent()

        compileContentForTest(content, environment)
    }
}

@KotlinCoreEnvironmentTest
class CompileWithThirdPartyLib(private val environment: KotlinEnvironmentContainer) {
    @Test
    fun `can compile snippet with junit5 api`() {
        val content = """
            import org.junit.jupiter.api.Assertions.assertEquals
            import org.junit.jupiter.api.Test

            class MyTest {
                @Test
                fun testSomething() {
                    assertEquals(1 + 2, 3)
                }
            }
        """.trimIndent()

        compileContentForTest(content, environment)
    }
}

@KotlinCoreEnvironmentTest(
    additionalJavaSourcePaths = ["java"],
)
class CompileWithBoth(private val environment: KotlinEnvironmentContainer) {
    @Test
    fun `can compile snippet with external library and test resources`() {
        val content = """
            import com.example.dummy.Foo
            import org.junit.jupiter.api.Assertions.assertNotEquals
            import org.junit.jupiter.api.Test

            class MyTest {
                @Test
                fun testSomething() {
                    val foo1 = Foo("abc")
                    val foo2 = Foo("def")
                    assertNotEquals(foo1, foo2)
                }
            }
        """.trimIndent()

        compileContentForTest(content, environment)
    }
}
