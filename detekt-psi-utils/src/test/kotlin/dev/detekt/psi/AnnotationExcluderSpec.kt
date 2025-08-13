package dev.detekt.psi

import dev.detekt.test.utils.KotlinAnalysisApiEngine
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

private val annotationsFileContent = """
            package dagger
            
            annotation class Component {
                annotation class Factory
            }
        """.trimIndent()

class AnnotationExcluderSpec() {

    @ParameterizedTest(
        name = "Given {0} is excluded when the {1} is found then the excluder returns {2} without analysis api"
    )
    @CsvFileSource(resources = ["/annotation_excluder.csv"])
    fun `all cases`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFileWithoutSession(annotation)
        val excluder = AnnotationExcluder(root = file, excludes = listOf(exclusion.toRegex()), fullAnalysis = false)

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @ParameterizedTest(
        name = "Given {0} is excluded when the {1} is found then the excluder returns {2} with analysis api"
    )
    @CsvFileSource(resources = ["/annotation_excluder.csv"])
    fun `all cases - Type Solving`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFile(annotation)

        val excluder = AnnotationExcluder(root = file, excludes = listOf(exclusion.toRegex()), fullAnalysis = true)

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @Nested
    inner class `special cases` {
        @Test
        fun `should not exclude when the annotation was not found`() {
            val (file, ktAnnotation) = createKtFile("@Component")
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin".toRegex()), fullAnalysis = false)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should not exclude when no annotations should be excluded`() {
            val (file, ktAnnotation) = createKtFile("@Component")
            val excluder = AnnotationExcluder(file, emptyList(), fullAnalysis = false)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should also exclude an annotation that is not imported`() {
            val (file, ktAnnotation) = createKtFileWithoutSession("@SinceKotlin")
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin".toRegex()), fullAnalysis = false)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
        }
    }

    @Nested
    inner class `difference between analysis api and no analysis api` {

        @Nested
        inner class `Don't mix annotations with the same name` {

            @Test
            fun `incorrect without analysis api`() {
                val (file, ktAnnotation) = createKtFileWithoutSession("""@Deprecated(message="text")""")
                val excluder = AnnotationExcluder(file, listOf("foo\\.Deprecated".toRegex()), fullAnalysis = false)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            fun `correct with analysis api`() {
                val (file, ktAnnotation) = createKtFile("""@Deprecated(message="text")""")
                val excluder = AnnotationExcluder(file, listOf("foo\\.Deprecated".toRegex()), fullAnalysis = true)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
            }
        }

        @Nested
        inner class `Know where a package ends` {
            val helloWorldAnnotationsKtFile = compileContentForTest(
                """
                    package com.Hello
                    
                    annotation class World
                """.trimIndent()
            )
            val file = compileContentForTest(
                """
                    package foo
                    
                    import com.Hello.World
                    
                    @World
                    fun function() = Unit
                """.trimIndent()
            )
            val ktAnnotation = file.findChildByClass(KtFunction::class.java)!!.annotationEntries.first()!!

            @Test
            fun `incorrect without analysis api`() {
                val excluder = AnnotationExcluder(file, listOf("Hello\\.World".toRegex()), fullAnalysis = false)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            @Disabled("This should be doable but it's not imlemented yet")
            fun `correct with analysis api`() {
                val excluder = AnnotationExcluder(file, listOf("Hello\\.World".toRegex()), fullAnalysis = true)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
            }
        }

        @Nested
        inner class `Know how to work with star imports` {
            val helloWorldAnnotationsContent = """
                    package com.hello
                    
                    annotation class World
                """.trimIndent()

            val fileContent = """
                    package foo
                    
                    import com.hello.*
                    
                    @World
                    fun function() = Unit
                """.trimIndent()


            @Test
            fun `incorrect without analysis api`() {
                val file = compileContentForTest(fileContent)
                val ktAnnotation = file.findAnnotationEntry()
                val excluder = AnnotationExcluder(file, listOf("foo\\.World".toRegex()), fullAnalysis = false)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()

                val excluder2 = AnnotationExcluder(file, listOf("com\\.hello\\.World".toRegex()), fullAnalysis = false)

                assertThat(excluder2.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            fun `correct with analysis api`() {
                val file = KotlinAnalysisApiEngine.compile(fileContent, listOf(helloWorldAnnotationsContent))
                val ktAnnotation = file.findAnnotationEntry()
                val excluder = AnnotationExcluder(file, listOf("foo\\.World".toRegex()), fullAnalysis = true)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()

                val excluder2 = AnnotationExcluder(file, listOf("com\\.hello\\.World".toRegex()), fullAnalysis = true)

                assertThat(excluder2.shouldExclude(listOf(ktAnnotation))).isTrue()
            }
        }
    }
}

private fun createKtFile(@Language("kotlin") annotation: String): Pair<KtFile, KtAnnotationEntry> {
    val file = KotlinAnalysisApiEngine.compile(fileContentForAnnotation(annotation), listOf(annotationsFileContent))

    return file to file.findAnnotationEntry()
}

private fun createKtFileWithoutSession(@Language("kotlin") annotation: String): Pair<KtFile, KtAnnotationEntry> {
    val file = compileContentForTest(fileContentForAnnotation(annotation))
    return file to file.findAnnotationEntry()
}

@Language("kotlin")
private fun fileContentForAnnotation(@Language("kotlin") annotation: String): String = """
            package foo
            
            import dagger.Component
            import dagger.Component.Factory
            
            $annotation
            fun function() = Unit
        """.trimIndent()

private fun KtFile.findAnnotationEntry(): KtAnnotationEntry =
    findChildByClass(KtFunction::class.java)!!.annotationEntries.first()
