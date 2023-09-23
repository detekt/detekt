package io.gitlab.arturbosch.detekt.core.suppressors

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Test

class AnnotationSuppressorFactorySpec {

    @Test
    fun `factory returns empty list if ignoreAnnotated and onlyAnnotated is not set`() {
        val suppressor = annotationSuppressorFactory(buildConfigAware(), BindingContext.EMPTY)

        assertThat(suppressor).isEmpty()
    }

    @Test
    fun `factory returns empty list if ignoreAnnotated and onlyAnnotated is set to empty`() {
        val suppressor = annotationSuppressorFactory(
            buildConfigAware(
                "ignoreAnnotated" to emptyList<String>(),
                "onlyAnnotated" to emptyList<String>()
            ),
            BindingContext.EMPTY,
        )

        assertThat(suppressor).isEmpty()
    }

    @Test
    fun `factory returns 1 element if ignoreAnnotated is set to a not empty list`() {
        val suppressor = annotationSuppressorFactory(
            buildConfigAware("ignoreAnnotated" to listOf("Composable")),
            BindingContext.EMPTY,
        )

        assertThat(suppressor).hasSize(1)
    }

    @Test
    fun `factory returns 1 element if onlyAnnotated is set to a not empty list`() {
        val suppressor = annotationSuppressorFactory(
            buildConfigAware("onlyAnnotated" to listOf("Composable")),
            BindingContext.EMPTY,
        )

        assertThat(suppressor).hasSize(1)
    }

    @Test
    fun `factory returns 2 elements if onlyAnnotated is set to a not empty list`() {
        val suppressor = annotationSuppressorFactory(
            buildConfigAware(
                "ignoreAnnotated" to listOf("Preview"),
                "onlyAnnotated" to listOf("Composable")
            ),
            BindingContext.EMPTY,
        )

        assertThat(suppressor).hasSize(2)
    }
}
