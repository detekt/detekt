package dev.detekt.psi

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class StringExtensionsKtSpec {
    @ParameterizedTest(name = "Given string pattern {0}, calling lastArgumentMatchesMarkdownUrlSyntax() returns true")
    @MethodSource("getAllowedMarkDownUrlPattern")
    fun matchesAllowedMarkdownUrlStringPattern(stringPattern: String) {
        assertThat(stringPattern.lastArgumentMatchesMarkdownUrlSyntax()).isTrue
    }

    @ParameterizedTest(name = "Given string pattern {0}, calling lastArgumentMatchesMarkdownUrlSyntax() returns false")
    @MethodSource("getDisAllowedMarkDownUrlPattern")
    fun doesNotMatchDisAllowedMarkdownUrlStringPattern(stringPattern: String) {
        assertThat(stringPattern.lastArgumentMatchesMarkdownUrlSyntax()).isFalse
    }

    @ParameterizedTest(
        name = "Given string pattern {0}, calling lastArgumentMatchesKotlinReferenceUrlSyntax() " +
            "returns true"
    )
    @MethodSource("getAllowedKotlinReferenceUrlPattern")
    fun matchesAllowedKotlinReferenceUrlStringPattern(stringPattern: String) {
        assertThat(stringPattern.lastArgumentMatchesKotlinReferenceUrlSyntax()).isTrue
    }

    @ParameterizedTest(
        name = "Given string pattern {0}, calling lastArgumentMatchesKotlinReferenceUrlSyntax() " +
            "returns false"
    )
    @MethodSource("getDisAllowedKotlinReferenceUrlPattern")
    fun doesNotMatchDisAllowedKotlinReferenceUrlStringPattern(stringPattern: String) {
        assertThat(stringPattern.lastArgumentMatchesKotlinReferenceUrlSyntax()).isFalse
    }

    companion object {
        @JvmStatic
        fun getAllowedMarkDownUrlPattern() = listOf(
            Arguments.of("""[Label](www.foo.com)"""),
            Arguments.of("""some other words [Label](www.foo.com)"""),
            Arguments.of("""[Label with space](www.foo.com)"""),
            Arguments.of("""[Label](www.foo.com "With title")"""),
            Arguments.of("[Label with tabs](www.foo.com\t\"With\ttitle\"\t)"),
            Arguments.of("""[Label](www.foo.com "With title")     """),
            Arguments.of("""[Label](www.foo.com "With title" )"""),
            Arguments.of("""[Label](www.foo.com "With title"  )"""),
            Arguments.of("""[Label](www.foo.com    "With title")"""),
            Arguments.of("""[Label with "" inside ''](/url 'title "and" title')"""),
            Arguments.of("""[Label with '' inside ""](/url "title 'and' title")"""),
            Arguments.of("""[Label ''](www.foo.com 'With title')"""),
            Arguments.of("""[Label ''](www.foo.com 'With title' )"""),
            Arguments.of("""[Label ()](/url (title))"""),
            Arguments.of("""[Label () with "" inside](/url (title "and" title2))"""),
            Arguments.of("""[Label () with '' inside](/url (title 'and' title2))"""),
            Arguments.of("""[Label](www.foo.com "With \"title\"")"""),
            Arguments.of("""[Label second word](www.foo.com "title")"""),
            Arguments.of("""[Label second \n word](www.foo.com "title")"""),
            Arguments.of("""See my [About](/about/)"""),
        )

        @JvmStatic
        fun getDisAllowedMarkDownUrlPattern() = listOf(
            Arguments.of("""[]()"""),
            Arguments.of("""[](foo)"""),
            Arguments.of("""[bar]()"""),
            Arguments.of("""[Label](www.foo.com"""),
            Arguments.of("""[Label](w ww.foo.com)"""),
            Arguments.of("""[Label](w ww.f  oo.com)"""),
            Arguments.of("[Label](www.f\too.com)"),
            Arguments.of("""[Label]www.foo.com)"""),
            Arguments.of("""[Label(www.foo.com)"""),
            Arguments.of("""Label](www.foo.com)"""),
            Arguments.of("""[Label](www.foo.com 'With title with unescaped ' as delimiter')"""),
            Arguments.of("""[Label](www.foo.com (With title with unescaped ' as delimiter)"""),
            Arguments.of("""[Label](www.foo.com "title1", "title2")"""),
            Arguments.of("""[Label](www.foo.com "title1", "title2")"""),
            Arguments.of("""[Label](www.foo.com "title1",)"""),
            Arguments.of("""[Label](www.foo.com , "With t1")"""),
            Arguments.of("""[Label](www.foo.com title1")"""),
            Arguments.of("""[Label](www.foo.com ! & "With t1")"""),
        )

        @JvmStatic
        fun getAllowedKotlinReferenceUrlPattern() = listOf(
            Arguments.of("""This is sample [Label][funName]"""),
            Arguments.of("""[Label][AClass.funName]"""),
            Arguments.of("""[Label][funName]"""),
            Arguments.of("""[Label][funName]"""),
            Arguments.of("""[Label] [funName]   """),
            Arguments.of("""[Label]   [funName]"""),
            Arguments.of("""[Label with words]   [funName]"""),
            Arguments.of("[Label with words]\t[funName]"),
            Arguments.of("""[funName]"""),
            Arguments.of("""[funName]   """),
        )

        @JvmStatic
        fun getDisAllowedKotlinReferenceUrlPattern() = listOf(
            Arguments.of("""[fun Name]"""),
            Arguments.of("""[fun  Name]"""),
            Arguments.of("[fun\tName]"),
            Arguments.of("""[funName ]"""),
            Arguments.of("""[ funName]"""),
        )
    }
}
