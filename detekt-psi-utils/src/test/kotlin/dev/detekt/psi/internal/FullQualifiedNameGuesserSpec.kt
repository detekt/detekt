package dev.detekt.psi.internal

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FullQualifiedNameGuesserSpec {

    @Nested
    inner class `With package` {
        private val sut = FullQualifiedNameGuesser(
            compileContentForTest(
                """
                    package foo
                    
                    import kotlin.jvm.JvmField
                    import kotlin.jvm.JvmStatic as Static
                    import java.io.*
                """.trimIndent()
            )
        )

        @Test
        fun import() {
            assertThat(sut.getFullQualifiedName("JvmField"))
                .containsExactlyInAnyOrder("kotlin.jvm.JvmField")
        }

        @Test
        fun `import with alias`() {
            assertThat(sut.getFullQualifiedName("Static"))
                .containsExactlyInAnyOrder("kotlin.jvm.JvmStatic")
        }

        @Test
        fun `import with alias but using real name`() {
            assertThat(sut.getFullQualifiedName("JvmStatic"))
                .containsExactlyInAnyOrder("foo.JvmStatic", "java.io.JvmStatic")
        }

        @Test
        fun `no import but maybe kotlin`() {
            assertThat(sut.getFullQualifiedName("Result"))
                .containsExactlyInAnyOrder("foo.Result", "kotlin.Result", "java.io.Result")
        }

        @Test
        fun `no import but not kotlin`() {
            assertThat(sut.getFullQualifiedName("Asdf"))
                .containsExactlyInAnyOrder("foo.Asdf", "java.io.Asdf")
        }

        @Test
        fun `import with subclass`() {
            assertThat(sut.getFullQualifiedName("JvmField.Factory"))
                .containsExactlyInAnyOrder("kotlin.jvm.JvmField.Factory")
        }

        @Test
        fun `alias-import with subclass`() {
            assertThat(sut.getFullQualifiedName("Static.Factory"))
                .containsExactlyInAnyOrder("kotlin.jvm.JvmStatic.Factory")
        }

        @Test
        fun `when not using the import`() {
            assertThat(sut.getFullQualifiedName("kotlin.jvm.JvmField"))
                .containsExactlyInAnyOrder(
                    "kotlin.jvm.JvmField",
                    "foo.kotlin.jvm.JvmField",
                    "java.io.kotlin.jvm.JvmField",
                )
        }
    }

    @Nested
    inner class `Without package` {
        private val sut = FullQualifiedNameGuesser(compileContentForTest("import kotlin.jvm.JvmField"))

        @Test
        fun `no import but maybe kotlin`() {
            assertThat(sut.getFullQualifiedName("Result"))
                .containsExactlyInAnyOrder("kotlin.Result")
        }

        @Test
        fun `no import and not kotlin`() {
            assertThat(sut.getFullQualifiedName("Asdf"))
                .isEmpty()
        }
    }
}
