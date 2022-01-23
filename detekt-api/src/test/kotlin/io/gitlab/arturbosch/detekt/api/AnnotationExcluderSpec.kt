package io.gitlab.arturbosch.detekt.api

import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.createPsiFactory
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.style.specification.describe

class AnnotationExcluderSpec : Spek({

    val psiFactory by memoized(CachingMode.SCOPE) { createPsiFactory() }

    describe("a kt file with some imports") {
        val file by memoized {
            compileContentForTest(
                """
                package foo

                import dagger.Component
                import dagger.Component.Factory
                """.trimIndent()
            )
        }

        context("All cases") {
            data class Case(val excludes: String, val annotation: String)

            mapOf(
                Case("Component", "@Component") to true,
                Case("Component", "@dagger.Component") to true,
                Case("Component", "@Factory") to true, // false positive
                Case("Component", "@Component.Factory") to true, // false positive
                Case("Component", "@dagger.Component.Factory") to true, // false positive
                Case("dagger.Component", "@Component") to true,
                Case("dagger.Component", "@dagger.Component") to true,
                Case("dagger.Component", "@Factory") to true, // false positive
                Case("dagger.Component", "@Component.Factory") to false,
                Case("dagger.Component", "@dagger.Component.Factory") to true, // false positive
                Case("Component.Factory", "@Component") to false,
                Case("Component.Factory", "@dagger.Component") to false,
                Case("Component.Factory", "@Factory") to true,
                Case("Component.Factory", "@Component.Factory") to true,
                Case("Component.Factory", "@dagger.Component.Factory") to true,
                Case("dagger.Component.Factory", "@Component") to false,
                Case("dagger.Component.Factory", "@dagger.Component") to false,
                Case("dagger.Component.Factory", "@Factory") to true,
                Case("dagger.Component.Factory", "@Component.Factory") to false, // false negative
                Case("dagger.Component.Factory", "@dagger.Component.Factory") to true,
                Case("Factory", "@Component") to false,
                Case("Factory", "@dagger.Component") to false,
                Case("Factory", "@Factory") to true,
                Case("Factory", "@Component.Factory") to true,
                Case("Factory", "@dagger.Component.Factory") to true,
                Case("dagger.*", "@Component") to true,
                Case("dagger.*", "@dagger.Component") to true,
                Case("dagger.*", "@Factory") to true,
                Case("dagger.*", "@Component.Factory") to false, // false positive
                Case("dagger.*", "@dagger.Component.Factory") to true,
                Case("*.Component.Factory", "@Component") to false,
                Case("*.Component.Factory", "@dagger.Component") to false,
                Case("*.Component.Factory", "@Factory") to true,
                Case("*.Component.Factory", "@Component.Factory") to false, // false positive
                Case("*.Component.Factory", "@dagger.Component.Factory") to true,
                Case("*.Component.*", "@Component") to false,
                Case("*.Component.*", "@dagger.Component") to false,
                Case("*.Component.*", "@Factory") to true,
                Case("*.Component.*", "@Component.Factory") to false, // false positive
                Case("*.Component.*", "@dagger.Component.Factory") to true,
                Case("foo.Component", "@Component") to false,
                Case("foo.Component", "@dagger.Component") to false,
                Case("foo.Component", "@Factory") to false,
                Case("foo.Component", "@Component.Factory") to false,
                Case("foo.Component", "@dagger.Component.Factory") to false,
            ).forEach { (case, expected) ->
                val (exclude, annotation) = case
                it("With exclude $exclude and annotation $annotation") {
                    val excluder = AnnotationExcluder(file, listOf(exclude))

                    val ktAnnotation = psiFactory.createAnnotationEntry(annotation)
                    assertThat(excluder.shouldExclude(listOf(ktAnnotation))).isEqualTo(expected)
                }
            }
        }

        context("special cases") {
            val annotation by memoized { psiFactory.createAnnotationEntry("@Component") }
            val sinceKotlinAnnotation by memoized { psiFactory.createAnnotationEntry("@SinceKotlin") }

            it("should not exclude when the annotation was not found") {
                val excluder = AnnotationExcluder(file, listOf("SinceKotlin"))
                assertThat(excluder.shouldExclude(listOf(annotation))).isFalse()
            }

            it("should not exclude when no annotations should be excluded") {
                val excluder = AnnotationExcluder(file, emptyList())
                assertThat(excluder.shouldExclude(listOf(annotation))).isFalse()
            }

            it("should also exclude an annotation that is not imported") {
                val excluder = AnnotationExcluder(file, listOf("SinceKotlin"))
                assertThat(excluder.shouldExclude(listOf(sinceKotlinAnnotation))).isTrue()
            }

            it("should exclude when the annotation was found with SplitPattern") {
                @Suppress("DEPRECATION")
                val excluder = AnnotationExcluder(file, SplitPattern("SinceKotlin"))
                assertThat(excluder.shouldExclude(listOf(sinceKotlinAnnotation))).isTrue()
            }
        }
    }
})
