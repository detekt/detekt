package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UnnecessaryAnnotationUseSiteTargetSpec {

    @Test
    @DisplayName("Unnecessary @param: in a property constructor")
    fun unnecessaryParamInPropertyConstructor() {
        val code = """
            class C(@param:Asdf private val foo: String)
            
            annotation class Asdf
        """.trimIndent()
        assertThat(UnnecessaryAnnotationUseSiteTarget(Config.empty).lint(code)).hasTextLocations("param:")
    }

    @Test
    @DisplayName("Unnecessary @param: in a constructor")
    fun unnecessaryParamInConstructor() {
        val code = """
            class C(@param:Asdf foo: String)
            
            annotation class Asdf
        """.trimIndent()
        assertThat(UnnecessaryAnnotationUseSiteTarget(Config.empty).lint(code)).hasTextLocations("param:")
    }

    @Test
    @DisplayName("Necessary @get:")
    fun unnecessaryGet() {
        val code = """
            class C(@get:Asdf private val foo: String)
            
            annotation class Asdf
        """.trimIndent()
        assertThat(UnnecessaryAnnotationUseSiteTarget(Config.empty).lint(code)).isEmpty()
    }

    @Test
    @DisplayName("Necessary @property:")
    fun necessaryProperty() {
        val code = """
            class C(@property:Asdf private val foo: String)
            
            annotation class Asdf
        """.trimIndent()
        assertThat(UnnecessaryAnnotationUseSiteTarget(Config.empty).lint(code)).isEmpty()
    }

    @Test
    @DisplayName("Unnecessary @property:")
    fun unnecessaryProperty() {
        val code = """
            class C {
                @property:Asdf private val foo: String = "bar"
            }
            
            annotation class Asdf
        """.trimIndent()
        assertThat(UnnecessaryAnnotationUseSiteTarget(Config.empty).lint(code)).hasTextLocations("property:")
    }

    @Test
    @DisplayName("Unnecessary @property: at a top level property")
    fun unnecessaryPropertyAtTopLevel() {
        val code = """
            @property:Asdf private val foo: String = "bar"
            
            annotation class Asdf
        """.trimIndent()
        assertThat(UnnecessaryAnnotationUseSiteTarget(Config.empty).lint(code)).hasTextLocations("property:")
    }
}
