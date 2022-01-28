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

        @ParameterizedTest(name = "Given {0} is excluded when the {1} is found then the excluder returns {2}")
        @CsvSource(
            value = [
                "Component,@Component,true",
                "Component,@dagger.Component,true",
                "Component,@Factory,true", // false positive
                "Component,@Component.Factory,true", // false positive
                "Component,@dagger.Component.Factory,true", // false positive
                "dagger.Component,@Component,true",
                "dagger.Component,@dagger.Component,true",
                "dagger.Component,@Factory,true", // false positive
                "dagger.Component,@Component.Factory,false",
                "dagger.Component,@dagger.Component.Factory,true", // false positive
                "Component.Factory,@Component,false",
                "Component.Factory,@dagger.Component,false",
                "Component.Factory,@Factory,true",
                "Component.Factory,@Component.Factory,true",
                "Component.Factory,@dagger.Component.Factory,true",
                "dagger.Component.Factory,@Component,false",
                "dagger.Component.Factory,@dagger.Component,false",
                "dagger.Component.Factory,@Factory,true",
                "dagger.Component.Factory,@Component.Factory,false", // false negative
                "dagger.Component.Factory,@dagger.Component.Factory,true",
                "Factory,@Component,false",
                "Factory,@dagger.Component,false",
                "Factory,@Factory,true",
                "Factory,@Component.Factory,true",
                "Factory,@dagger.Component.Factory,true",
                "dagger.*,@Component,true",
                "dagger.*,@dagger.Component,true",
                "dagger.*,@Factory,true",
                "dagger.*,@Component.Factory,false", // false positive
                "dagger.*,@dagger.Component.Factory,true",
                "*.Component.Factory,@Component,false",
                "*.Component.Factory,@dagger.Component,false",
                "*.Component.Factory,@Factory,true",
                "*.Component.Factory,@Component.Factory,false", // false positive
                "*.Component.Factory,@dagger.Component.Factory,true",
                "*.Component.*,@Component,false",
                "*.Component.*,@dagger.Component,false",
                "*.Component.*,@Factory,true",
                "*.Component.*,@Component.Factory,false", // false positive
                "*.Component.*,@dagger.Component.Factory,true",
                "foo.Component,@Component,false",
                "foo.Component,@dagger.Component,false",
                "foo.Component,@Factory,false",
                "foo.Component,@Component.Factory,false",
                "foo.Component,@dagger.Component.Factory,false",
            ]
        )
        fun `all cases`(exclusion: String, annotation: String, shouldExclude: Boolean) {
            val excluder = AnnotationExcluder(file, listOf(exclusion))

            val ktAnnotation = psiFactory.createAnnotationEntry(annotation)
            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
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
