package io.github.detekt.psi

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FullQualifiedNameGuesserSpec {

    @Nested
    class `With package` {
        private val sut = FullQualifiedNameGuesser(
            compileContentForTest(
                """
                package foo

                import kotlin.jvm.JvmField
                import kotlin.jvm.JvmStatic as Static
                """.trimIndent()
            )
        )

        @Test
        fun `import`() {
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
                .containsExactlyInAnyOrder("foo.JvmStatic")
        }

        @Test
        fun `no import but maybe kotlin`() {
            assertThat(sut.getFullQualifiedName("Result"))
                .containsExactlyInAnyOrder("foo.Result", "kotlin.Result")
        }

        @Test
        fun `no import but not kotlin`() {
            assertThat(sut.getFullQualifiedName("Asdf"))
                .containsExactlyInAnyOrder("foo.Asdf")
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
    }

    @Nested
    class `Without package` {
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
