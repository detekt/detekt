package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail
import io.gitlab.arturbosch.detekt.test.KtTestCompiler

class AnnotationExcluderSpec : Spek({
  given("a kt file with some imports") {
    val jvmFieldAnnotation = FACTORY.createAnnotationEntry("JvmField")
    val sinceKotlinAnnotation = FACTORY.createAnnotationEntry("SinceKotlin")

    val file = KtTestCompiler.compileFromContent("""
			package foo

			import kotlin.jvm.JvmField
		""".trimIndent())

    it("should exclude when the annotation was found") {
      val excluder = AnnotationExcluder(file, Excludes("JvmField"))
      assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isTrue()
    }

    it("should not exclude when the annotation was not found") {
      val excluder = AnnotationExcluder(file, Excludes("Jvm Field"))
      assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isFalse()
    }

    it("should not exclude when no annotations should be excluded") {
      val excluder = AnnotationExcluder(file, Excludes(""))
      assertThat(excluder.shouldExclude(listOf(jvmFieldAnnotation))).isFalse()
    }

    it("should not exclude an annotation that is not imported") {
      val excluder = AnnotationExcluder(file, Excludes("SinceKotlin"))
      assertThat(excluder.shouldExclude(listOf(sinceKotlinAnnotation))).isFalse()
    }
  }
})
