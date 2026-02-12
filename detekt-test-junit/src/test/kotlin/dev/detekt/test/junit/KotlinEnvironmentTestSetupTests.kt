package dev.detekt.test.junit

import dev.detekt.generated.TestBuildConfig.OKIO_JAR_PATH
import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.compileContentForTest
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest(additionalJavaSourcePaths = ["java"])
class CompileWithSourcesTest(private val environment: KotlinEnvironmentContainer) {
    @Test
    fun `can compile snippet`() {
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

@KotlinCoreEnvironmentTest(additionalJarPaths = [OKIO_JAR_PATH])
class CompileWithThirdPartyLib(private val environment: KotlinEnvironmentContainer) {
    @Test
    fun `can compile snippet`() {
        val content = """
            import okio.ByteString
            import okio.ByteString.Companion.decodeBase64

            fun test(): ByteString? {
                val b64 = "TWFueSBoYW5kcyBtYWtlIGxpZ2h0IHdvcmsu"
                return b64.decodeBase64()
            }
        """.trimIndent()

        compileContentForTest(content, environment)
    }
}

@KotlinCoreEnvironmentTest(
    additionalJavaSourcePaths = ["java"],
    additionalJarPaths = [OKIO_JAR_PATH],
)
class CompileWithBoth(private val environment: KotlinEnvironmentContainer) {
    @Test
    fun `can compile snippet`() {
        val content = """
            import com.example.dummy.Foo
            import okio.ByteString
            import okio.ByteString.Companion.decodeBase64

            fun test(): ByteString? {
                val foo = Foo("abc")
                return foo.name.decodeBase64()
            }
        """.trimIndent()

        compileContentForTest(content, environment)
    }
}
