package dev.detekt.psi

import dev.detekt.test.utils.KotlinAnalysisApiEngine
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class AnnotationExcluderSpec {

    @ParameterizedTest(
        name = "Given {0} is excluded when the {1} is found then the excluder returns {2} without Analysis API"
    )
    @CsvFileSource(resources = ["/annotation_excluder.csv"])
    fun `all cases`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFile(annotation, enableAnalysisApi = false)
        val excluder = AnnotationExcluder(file, listOf(exclusion.toRegex()), false)

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @ParameterizedTest(
        name = "Given {0} is excluded when the {1} is found then the excluder returns {2} with Analysis API"
    )
    @CsvFileSource(resources = ["/annotation_excluder.csv"])
    fun `all cases - AnalysisAPI`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFile(annotation, enableAnalysisApi = true)
        val excluder = AnnotationExcluder(file, listOf(exclusion.toRegex()), true)

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @Nested
    inner class `special cases` {
        @Test
        fun `should not exclude when the annotation was not found`() {
            val (file, ktAnnotation) = createKtFile("@Component", enableAnalysisApi = false)
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin".toRegex()), false)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should not exclude when no annotations should be excluded`() {
            val (file, ktAnnotation) = createKtFile("@Component", enableAnalysisApi = false)
            val excluder = AnnotationExcluder(file, emptyList(), false)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should also exclude an annotation that is not imported`() {
            val (file, ktAnnotation) = createKtFile("@SinceKotlin", enableAnalysisApi = false)
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin".toRegex()), false)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
        }
    }

    @Nested
    inner class `difference between Analysis API and no Analysis API` {

        @Nested
        inner class `Don't mix annotations with the same name` {

            @Test
            fun `incorrect without Analysis API`() {
                val (file, ktAnnotation) = createKtFile("@Deprecated", enableAnalysisApi = false)
                val excluder = AnnotationExcluder(file, listOf("foo\\.Deprecated".toRegex()), false)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            fun `correct with Analysis API`() {
                val (file, ktAnnotation) = createKtFile("@Deprecated(\"\")", enableAnalysisApi = true)
                val excluder = AnnotationExcluder(file, listOf("foo\\.Deprecated".toRegex()), true)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
            }
        }

        @Nested
        inner class `Know where a package ends` {
            val helloWorldAnnotationsCode = """
                package com.Hello

                annotation class World
            """.trimIndent()
            val code = """
                package foo

                import com.Hello.World

                @World
                fun function() = Unit
            """.trimIndent()

            @Test
            fun `incorrect without Analysis API`() {
                val file = compileContentForTest(code)
                val ktAnnotation = file.annotationEntry()
                val excluder = AnnotationExcluder(file, listOf("Hello\\.World".toRegex()), false)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            @Disabled("This should be doable but it's not imlemented yet")
            fun `correct with Analysis API`() {
                val file = KotlinAnalysisApiEngine.compile(code, listOf(helloWorldAnnotationsCode))
                val ktAnnotation = file.annotationEntry()
                val excluder = AnnotationExcluder(file, listOf("Hello\\.World".toRegex()), true)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
            }
        }

        @Nested
        inner class `Know how to work with star imports` {
            val helloWorldAnnotationsKtFile = """
                package com.hello

                annotation class World
            """.trimIndent()
            val file = """
                package foo

                import com.hello.*

                @World
                fun function() = Unit
            """.trimIndent()

            @Test
            fun `incorrect without Analysis API`() {
                val file = compileContentForTest(file)
                val ktAnnotation = file.annotationEntry()
                val excluder = AnnotationExcluder(file, listOf("foo\\.World".toRegex()), false)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()

                val excluder2 = AnnotationExcluder(file, listOf("com\\.hello\\.World".toRegex()), false)

                assertThat(excluder2.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            fun `correct with Analysis API`() {
                val file = KotlinAnalysisApiEngine.compile(file, listOf(helloWorldAnnotationsKtFile))
                val ktAnnotation = file.annotationEntry()
                val excluder = AnnotationExcluder(file, listOf("foo\\.World".toRegex()), true)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()

                val excluder2 = AnnotationExcluder(file, listOf("com\\.hello\\.World".toRegex()), true)

                assertThat(excluder2.shouldExclude(listOf(ktAnnotation))).isTrue()
            }
        }
    }
}

private fun createKtFile(annotation: String, enableAnalysisApi: Boolean): Pair<KtFile, KtAnnotationEntry> {
    val code = """
        package foo
        
        import dagger.Component
        import dagger.Component.Factory
        
        $annotation
        fun function() = Unit
    """.trimIndent()
    val file = if (enableAnalysisApi) {
        KotlinAnalysisApiEngine.compile(
            code,
            listOf(
                """
                    package dagger
                    
                    annotation class Component {
                        annotation class Factory
                    }
                """.trimIndent(),
            ),
        )
    } else {
        compileContentForTest(code)
    }

    return file to file.annotationEntry()
}

private fun KtFile.annotationEntry(): KtAnnotationEntry =
    findChildByClass(KtFunction::class.java)!!.annotationEntries.first()!!
