package io.gitlab.arturbosch.detekt.formatting

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoWildcardImports
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NoWildcardImportsSpec {
    @Nested
    inner class PackagesToUseImportOnDemandPropertyNotSet {

        private lateinit var subject: NoWildcardImports

        @BeforeEach
        fun createSubject() {
            subject = NoWildcardImports(Config.empty)
        }

        @Test
        fun `Wildcard imports are detected`() {
            val code = """
                import a.*
                import a.b.c.*
                import a.b
                import foo.bar.`**`
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(2)
        }

        @Test
        fun `Wildcard imports on packages which are accepted by IntelliJ Default are not detected`() {
            val code = """
                import a.b
                import kotlinx.android.synthetic.main.layout_name.*
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class PackagesToUseImportOnDemandPropertySet {
        @Test
        fun `Given that the property is set with value 'unset' then packages which are accepted by IntelliJ Default are not detected`() {
            val code = """
                import a.b
                import kotlinx.android.synthetic.main.layout_name.*
                import react.*
                import react.dom.*
            """.trimIndent()

            assertThat(
                NoWildcardImports(TestConfig("packagesToUseImportOnDemandProperty" to "unset")).lint(
                    code
                )
            ).hasSize(2)
        }

        @Test
        fun `Given that the property is set to some packages exclusive subpackages then wildcard imports for those directories are not detected`() {
            val code = """
                import a.b
                import kotlinx.android.synthetic.main.layout_name.*
                import react.*
                import react.dom.*
            """.trimIndent()
            assertThat(
                NoWildcardImports(TestConfig("packagesToUseImportOnDemandProperty" to "react.*,react.dom.*")).lint(
                    code
                )
            ).hasSize(1)
        }

        @Test
        fun `Given that the property is set to some packages inclusive subpackages then wildcard imports for those directories are not detected`() {
            val code = """
                import a.b
                import kotlinx.android.synthetic.main.layout_name.*
                import react.*
                import react.dom.*
            """.trimIndent()

            assertThat(
                NoWildcardImports(TestConfig("packagesToUseImportOnDemandProperty" to "react.**")).lint(
                    code
                )
            ).hasSize(1)
        }

        @Test
        fun `Given that property is set without a value then the packages which otherwise would be accepted by IntelliJ Default are detected`() {
            val code = """
                import a.b
                import kotlinx.android.synthetic.main.layout_name.*
            """.trimIndent()

            assertThat(
                NoWildcardImports(TestConfig("packagesToUseImportOnDemandProperty" to "")).lint(
                    code
                )
            ).hasSize(1)
        }
    }
}
