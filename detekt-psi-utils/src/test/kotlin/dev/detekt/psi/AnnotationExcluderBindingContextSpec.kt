package dev.detekt.psi

import dev.detekt.test.createBindingContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

@KotlinCoreEnvironmentTest
class AnnotationExcluderBindingContextSpec(private val env: KotlinEnvironmentContainer) {
    private val annotationsKtFile = compileContentForTest(
        """
            package dagger
            
            annotation class Component {
                annotation class Factory
            }
        """.trimIndent()
    )

    @ParameterizedTest(
        name = "Given {0} is excluded when the {1} is found then the excluder returns {2} without type solving"
    )
    @CsvFileSource(resources = ["/annotation_excluder.csv"])
    fun `all cases`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFile(annotation)
        val excluder = AnnotationExcluderBindingContext(file, listOf(exclusion.toRegex()), BindingContext.EMPTY)

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @ParameterizedTest(
        name = "Given {0} is excluded when the {1} is found then the excluder returns {2} with type solving"
    )
    @CsvFileSource(resources = ["/annotation_excluder.csv"])
    fun `all cases - Type Solving`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFile(annotation)
        val binding = env.createBindingContext(listOf(file, annotationsKtFile))
        val excluder = AnnotationExcluderBindingContext(file, listOf(exclusion.toRegex()), binding)

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @Nested
    inner class `special cases` {
        @Test
        fun `should not exclude when the annotation was not found`() {
            val (file, ktAnnotation) = createKtFile("@Component")
            val excluder = AnnotationExcluderBindingContext(file, listOf("SinceKotlin".toRegex()), BindingContext.EMPTY)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should not exclude when no annotations should be excluded`() {
            val (file, ktAnnotation) = createKtFile("@Component")
            val excluder = AnnotationExcluderBindingContext(file, emptyList(), BindingContext.EMPTY)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should also exclude an annotation that is not imported`() {
            val (file, ktAnnotation) = createKtFile("@SinceKotlin")
            val excluder = AnnotationExcluderBindingContext(file, listOf("SinceKotlin".toRegex()), BindingContext.EMPTY)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
        }
    }

    @Nested
    inner class `difference between type solving and no type solving` {

        @Nested
        inner class `Don't mix annotations with the same name` {

            @Test
            fun `incorrect without type solving`() {
                val (file, ktAnnotation) = createKtFile("@Deprecated")
                val excluder =
                    AnnotationExcluderBindingContext(file, listOf("foo\\.Deprecated".toRegex()), BindingContext.EMPTY)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            fun `correct without type solving`() {
                val (file, ktAnnotation) = createKtFile("@Deprecated")
                val binding = env.createBindingContext(listOf(file, annotationsKtFile))
                val excluder = AnnotationExcluderBindingContext(file, listOf("foo\\.Deprecated".toRegex()), binding)

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
            fun `incorrect without type solving`() {
                val excluder =
                    AnnotationExcluderBindingContext(file, listOf("Hello\\.World".toRegex()), BindingContext.EMPTY)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            @Disabled("This should be doable but it's not imlemented yet")
            fun `correct with type solving`() {
                val binding = env.createBindingContext(listOf(file, helloWorldAnnotationsKtFile))
                val excluder = AnnotationExcluderBindingContext(file, listOf("Hello\\.World".toRegex()), binding)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
            }
        }

        @Nested
        inner class `Know how to work with star imports` {
            val helloWorldAnnotationsKtFile = compileContentForTest(
                """
                    package com.hello
                    
                    annotation class World
                """.trimIndent()
            )
            val file = compileContentForTest(
                """
                    package foo
                    
                    import com.hello.*
                    
                    @World
                    fun function() = Unit
                """.trimIndent()
            )
            val ktAnnotation = file.findChildByClass(KtFunction::class.java)!!.annotationEntries.first()!!

            @Test
            fun `incorrect without type solving`() {
                val excluder =
                    AnnotationExcluderBindingContext(file, listOf("foo\\.World".toRegex()), BindingContext.EMPTY)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()

                val excluder2 = AnnotationExcluderBindingContext(
                    file,
                    listOf("com\\.hello\\.World".toRegex()),
                    BindingContext.EMPTY
                )

                assertThat(excluder2.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @Test
            fun `correct with type solving`() {
                val binding = env.createBindingContext(listOf(file, helloWorldAnnotationsKtFile))
                val excluder = AnnotationExcluderBindingContext(file, listOf("foo\\.World".toRegex()), binding)

                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()

                val excluder2 = AnnotationExcluderBindingContext(file, listOf("com\\.hello\\.World".toRegex()), binding)

                assertThat(excluder2.shouldExclude(listOf(ktAnnotation))).isTrue()
            }
        }
    }
}

private fun createKtFile(annotation: String): Pair<KtFile, KtAnnotationEntry> {
    val file = compileContentForTest(
        """
            package foo
            
            import dagger.Component
            import dagger.Component.Factory
            
            $annotation
            fun function() = Unit
        """.trimIndent()
    )

    return file to file.findChildByClass(KtFunction::class.java)!!.annotationEntries.first()
}
