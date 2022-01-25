package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.createPsiFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AnnotationExcluderSpec {

    private val psiFactory = createPsiFactory()

    @Nested
    inner class `a kt file with some imports` {
        private val file = compileContentForTest(
            """
                package foo

                import dagger.Component
                import dagger.Component.Factory
            """.trimIndent()
        )

        @Nested
        inner class `All cases` {

            @ParameterizedTest
            @CsvSource(
                value = [
                    "Component,@Component",
                    "Component,@dagger.Component",
                    "Component,@Factory", // false positive
                    "Component,@Component.Factory", // false positive
                    "Component,@dagger.Component.Factory", // false positive
                    "dagger.Component,@Component",
                    "dagger.Component,@dagger.Component",
                    "dagger.Component,@Factory", // false positive
                    "dagger.Component,@dagger.Component.Factory", // false positive
                    "Component.Factory,@Factory",
                    "Component.Factory,@Component.Factory",
                    "Component.Factory,@dagger.Component.Factory",
                    "dagger.Component.Factory,@Factory",
                    "dagger.Component.Factory,@dagger.Component.Factory",
                    "Factory,@Factory",
                    "Factory,@Component.Factory",
                    "Factory,@dagger.Component.Factory",
                    "dagger.*,@Component",
                    "dagger.*,@dagger.Component",
                    "dagger.*,@Factory",
                    "dagger.*,@dagger.Component.Factory",
                    "*.Component.Factory,@Factory",
                    "*.Component.Factory,@dagger.Component.Factory",
                    "*.Component.*,@Factory",
                    "*.Component.*,@dagger.Component.Factory",
                ]
            )
            fun `should exclude`(exclusion: String, annotation: String) {
                val excluder = AnnotationExcluder(file, listOf(exclusion))

                val ktAnnotation = psiFactory.createAnnotationEntry(annotation)
                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
            }

            @ParameterizedTest
            @CsvSource(
                value = [
                    "dagger.Component,@Component.Factory",
                    "Component.Factory,@Component",
                    "Component.Factory,@dagger.Component",
                    "dagger.Component.Factory,@Component",
                    "dagger.Component.Factory,@dagger.Component",
                    "dagger.Component.Factory,@Component.Factory", // false negative
                    "Factory,@Component",
                    "Factory,@dagger.Component",
                    "dagger.*,@Component.Factory", // false positive
                    "*.Component.Factory,@Component",
                    "*.Component.Factory,@dagger.Component",
                    "*.Component.Factory,@Component.Factory", // false positive
                    "*.Component.*,@Component",
                    "*.Component.*,@dagger.Component",
                    "*.Component.*,@Component.Factory", // false positive
                    "foo.Component,@Component",
                    "foo.Component,@dagger.Component",
                    "foo.Component,@Factory",
                    "foo.Component,@Component.Factory",
                    "foo.Component,@dagger.Component.Factory",
                ]
            )
            fun `should not exclude`(exclusion: String, annotation: String) {
                val excluder = AnnotationExcluder(file, listOf(exclusion))

                val ktAnnotation = psiFactory.createAnnotationEntry(annotation)
                assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
            }
        }

        @Nested
        inner class `special cases` {
            private val annotation = psiFactory.createAnnotationEntry("@Component")
            private val sinceKotlinAnnotation = psiFactory.createAnnotationEntry("@SinceKotlin")

            @Test
            fun `should not exclude when the annotation was not found`() {
                val excluder = AnnotationExcluder(file, listOf("SinceKotlin"))
                assertThat(excluder.shouldExclude(listOf(annotation))).isFalse()
            }

            @Test
            fun `should not exclude when no annotations should be excluded`() {
                val excluder = AnnotationExcluder(file, emptyList())
                assertThat(excluder.shouldExclude(listOf(annotation))).isFalse()
            }

            @Test
            fun `should also exclude an annotation that is not imported`() {
                val excluder = AnnotationExcluder(file, listOf("SinceKotlin"))
                assertThat(excluder.shouldExclude(listOf(sinceKotlinAnnotation))).isTrue()
            }

            @Test
            fun `should exclude when the annotation was found with SplitPattern`() {
                @Suppress("DEPRECATION")
                val excluder = AnnotationExcluder(file, SplitPattern("SinceKotlin"))
                assertThat(excluder.shouldExclude(listOf(sinceKotlinAnnotation))).isTrue()
            }
        }
    }
}
