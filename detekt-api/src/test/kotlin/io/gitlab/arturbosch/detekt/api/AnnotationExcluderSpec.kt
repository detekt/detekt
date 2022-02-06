package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class AnnotationExcluderSpec {
    @ParameterizedTest(name = "Given {0} is excluded when the {1} is found then the excluder returns {2}")
    @CsvSource(
        value = [
            "Component,@Component,true",
            "Component,@dagger.Component,true",
            "Component,@Factory,false",
            "Component,@Component.Factory,false",
            "Component,@dagger.Component.Factory,false",

            "dagger.Component,@Component,true",
            "dagger.Component,@dagger.Component,true",
            "dagger.Component,@Factory,false",
            "dagger.Component,@Component.Factory,false",
            "dagger.Component,@dagger.Component.Factory,false",

            "Component.Factory,@Component,false",
            "Component.Factory,@dagger.Component,false",
            "Component.Factory,@Factory,true",
            "Component.Factory,@Component.Factory,true",
            "Component.Factory,@dagger.Component.Factory,true",

            "dagger.Component.Factory,@Component,false",
            "dagger.Component.Factory,@dagger.Component,false",
            "dagger.Component.Factory,@Factory,true",
            "dagger.Component.Factory,@Component.Factory,true",
            "dagger.Component.Factory,@dagger.Component.Factory,true",

            "Factory,@Component,false",
            "Factory,@dagger.Component,false",
            "Factory,@Factory,true",
            "Factory,@Component.Factory,true",
            "Factory,@dagger.Component.Factory,true",

            "dagger.*,@Component,true",
            "dagger.*,@dagger.Component,true",
            "dagger.*,@Factory,true",
            "dagger.*,@Component.Factory,true",
            "dagger.*,@dagger.Component.Factory,true",

            "*.Component.Factory,@Component,false",
            "*.Component.Factory,@dagger.Component,false",
            "*.Component.Factory,@Factory,true",
            "*.Component.Factory,@Component.Factory,true",
            "*.Component.Factory,@dagger.Component.Factory,true",

            "*.Component.*,@Component,false",
            "*Component*,@Component,true",
            "*Component,@Component,true",
            "*.Component.*,@dagger.Component,false",
            "*.Component.*,@Factory,true",
            "*.Component.*,@Component.Factory,true",
            "*.Component.*,@dagger.Component.Factory,true",

            "foo.Component,@Component,false",
            "foo.Component,@dagger.Component,false",
            "foo.Component,@Factory,false",
            "foo.Component,@Component.Factory,false",
            "foo.Component,@dagger.Component.Factory,false",
        ]
    )
    fun `all cases`(exclusion: String, annotation: String, shouldExclude: Boolean) {
        val (file, ktAnnotation) = createKtFile(annotation)
        val excluder = AnnotationExcluder(file, listOf(exclusion))

        assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(shouldExclude)
    }

    @Nested
    inner class `special cases` {
        @Test
        fun `should not exclude when the annotation was not found`() {
            val (file, ktAnnotation) = createKtFile("@Component")
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin"))

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should not exclude when no annotations should be excluded`() {
            val (file, ktAnnotation) = createKtFile("@Component")
            val excluder = AnnotationExcluder(file, emptyList())

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isFalse()
        }

        @Test
        fun `should also exclude an annotation that is not imported`() {
            val (file, ktAnnotation) = createKtFile("@SinceKotlin")
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin"))

            assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isTrue()
        }

        @Test
        fun `should exclude when the annotation was found with SplitPattern`() {
            val (file, ktAnnotation) = createKtFile("@SinceKotlin")
            val excluder = @Suppress("DEPRECATION") AnnotationExcluder(file, SplitPattern("SinceKotlin"))

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
