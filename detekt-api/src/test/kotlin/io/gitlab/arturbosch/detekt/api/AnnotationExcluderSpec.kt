package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.getContextForPaths
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

@KotlinCoreEnvironmentTest
class AnnotationExcluderSpec(private val env: KotlinCoreEnvironment) {
    private val annotationsKtFile = compileContentForTest(
        """
            package dagger

            annotation class Component {
                annotation class Factory
            }
        """.trimIndent()
    )

    @ParameterizedTest(name = "Given {0} is excluded when the {1} is found then the excluder returns {2} without type solving")
    @CsvFileSource(resources = ["/annotation_excluder.csv"])
    fun `all cases`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFile(annotation)
        val excluder = AnnotationExcluder(file, listOf(exclusion.toRegex()), BindingContext.EMPTY)

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @ParameterizedTest(name = "Given {0} is excluded when the {1} is found then the excluder returns {2} with type solving")
    @CsvFileSource(resources = ["/annotation_excluder.csv"])
    fun `all cases - Type Solving`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFile(annotation)
        val binding = env.getContextForPaths(listOf(file, annotationsKtFile))
        val excluder = AnnotationExcluder(file, listOf(exclusion.toRegex()), binding)

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @Nested
    inner class `special cases` {
        @Test
        fun `should not exclude when the annotation was not found`() {
            val (file, ktAnnotation) = createKtFile("@Component")
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin".toRegex()), BindingContext.EMPTY)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should not exclude when no annotations should be excluded`() {
            val (file, ktAnnotation) = createKtFile("@Component")
            val excluder = AnnotationExcluder(file, emptyList(), BindingContext.EMPTY)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should also exclude an annotation that is not imported`() {
            val (file, ktAnnotation) = createKtFile("@SinceKotlin")
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin".toRegex()), BindingContext.EMPTY)

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
        }

        @Test
        fun `should exclude when the annotation was found with SplitPattern`() {
            val (file, ktAnnotation) = createKtFile("@SinceKotlin")
            val excluder = @Suppress("DEPRECATION") AnnotationExcluder(file, SplitPattern("SinceKotlin"))

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
        }

        @Test
        fun `should exclude when the annotation was found with List of Strings`() {
            val (file, ktAnnotation) = createKtFile("@SinceKotlin")
            val excluder = @Suppress("DEPRECATION") AnnotationExcluder(file, listOf("SinceKo*"))

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
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
