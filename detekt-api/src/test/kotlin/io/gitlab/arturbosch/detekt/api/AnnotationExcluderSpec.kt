package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class AnnotationExcluderSpec : Spek({

    val psiFactory = KtTestCompiler.createPsiFactory()

    describe("a kt file with some imports") {
        val jvmFieldAnnotation = psiFactory.createAnnotationEntry("@JvmField")
        val fullyQualifiedJvmFieldAnnotation = psiFactory.createAnnotationEntry("@kotlin.jvm.JvmField")
        val sinceKotlinAnnotation = psiFactory.createAnnotationEntry("@SinceKotlin")

        val file = compileContentForTest("""
            package foo

            import kotlin.jvm.JvmField
        """.trimIndent())

        it("should exclude when the annotation was found") {
            val excluder = AnnotationExcluder(file, listOf("JvmField"))
            assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isTrue()
        }

        it("should not exclude when the annotation was not found") {
            val excluder = AnnotationExcluder(file, listOf("Jvm Field"))
            assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isFalse()
        }

        it("should not exclude when no annotations should be excluded") {
            val excluder = AnnotationExcluder(file, listOf())
            assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isFalse()
        }

        it("should exclude when the annotation was found with its fully qualified name") {
            val excluder = AnnotationExcluder(file, listOf("JvmField"))
            assertThat(excluder.shouldExclude(listOf(fullyQualifiedJvmFieldAnnotation))).isTrue()
        }

        it("should also exclude an annotation that is not imported") {
            val excluder = AnnotationExcluder(file, listOf("SinceKotlin"))
            assertThat(excluder.shouldExclude(listOf(sinceKotlinAnnotation))).isTrue()
        }

        it("should exclude when the annotation was found with SplitPattern") {
            @Suppress("Deprecation")
            val excluder = AnnotationExcluder(file, SplitPattern("JvmField"))
            assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isTrue()
        }
    }
})
