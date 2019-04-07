package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class AnnotationExcluderSpec : Spek({
    describe("a kt file with some imports") {
        val jvmFieldAnnotation = psiFactory.createAnnotationEntry("@JvmField")
        val sinceKotlinAnnotation = psiFactory.createAnnotationEntry("@SinceKotlin")

        val file = compileContentForTest("""
			package foo

			import kotlin.jvm.JvmField
		""".trimIndent())

        it("should exclude when the annotation was found") {
            val excluder = AnnotationExcluder(file, SplitPattern("JvmField"))
            assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isTrue()
        }

        it("should not exclude when the annotation was not found") {
            val excluder = AnnotationExcluder(file, SplitPattern("Jvm Field"))
            assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isFalse()
        }

        it("should not exclude when no annotations should be excluded") {
            val excluder = AnnotationExcluder(file, SplitPattern(""))
            assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isFalse()
        }

        it("should not exclude an annotation that is not imported") {
            val excluder = AnnotationExcluder(file, SplitPattern("SinceKotlin"))
            assertThat(excluder.shouldExclude(listOf(sinceKotlinAnnotation))).isFalse()
        }
    }
})
