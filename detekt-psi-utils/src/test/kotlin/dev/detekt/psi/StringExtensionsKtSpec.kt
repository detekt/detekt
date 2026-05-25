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
        fun getAllowedMarkDownUrlPattern() =
            listOf(
                """[Label](www.foo.com)""",
                """some other words [Label](www.foo.com)""",
                """[Label with space](www.foo.com)""",
                """[Label](www.foo.com "With title")""",
                "[Label with tabs](www.foo.com\t\"With\ttitle\"\t)",
                """[Label](www.foo.com "With title")     """,
                """[Label](www.foo.com "With title" )""",
                """[Label](www.foo.com "With title"  )""",
                """[Label](www.foo.com    "With title")""",
                """[Label with "" inside ''](/url 'title "and" title')""",
                """[Label with '' inside ""](/url "title 'and' title")""",
                """[Label ''](www.foo.com 'With title')""",
                """[Label ''](www.foo.com 'With title' )""",
                """[Label ()](/url (title))""",
                """[Label () with "" inside](/url (title "and" title2))""",
                """[Label () with '' inside](/url (title 'and' title2))""",
                """[Label](www.foo.com "With \"title\"")""",
                """[Label second word](www.foo.com "title")""",
                """[Label second \n word](www.foo.com "title")""",
                """See my [About](/about/)""",
            )
                .flatMap {
                    listOf(
                        it,
                        "${it.substringBeforeLast(")")}).${it.substringAfterLast(")")}",
                        "${it.substringBeforeLast(")")}),${it.substringAfterLast(")")}",
                    )
                }
                .map { Arguments.of(it) }

        @JvmStatic
        fun getDisAllowedMarkDownUrlPattern() =
            listOf(
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
                Arguments.of("""[Label](www.foo.com "With title")     ."""),
                Arguments.of("""[Label](www.foo.com "With title")     ,"""),
            )

        @JvmStatic
        fun getAllowedKotlinReferenceUrlPattern() =
            listOf(
                """This is sample [Label][funName]""",
                """[Label][AClass.funName]""",
                """[Label][funName]""",
                """[Label][funName]""",
                """[Label] [funName]   """,
                """[Label]   [funName]""",
                """[Label with words]   [funName]""",
                "[Label with words]\t[funName]",
                """[funName]""",
                """[funName]   """,
            )
                .flatMap {
                    listOf(
                        it,
                        "${it.substringBeforeLast(")")}).${it.substringAfterLast(")")}",
                        "${it.substringBeforeLast(")")}),${it.substringAfterLast(")")}",
                    )
                }
                .map { Arguments.of(it) }

        @JvmStatic
        fun getDisAllowedKotlinReferenceUrlPattern() =
            listOf(
                Arguments.of("""[fun Name]"""),
                Arguments.of("""[fun  Name]"""),
                Arguments.of("[fun\tName]"),
                Arguments.of("""[funName ]"""),
                Arguments.of("""[ funName]"""),
                Arguments.of("""[funName]   ."""),
                Arguments.of("""[funName]   ,"""),
            )
    }
}
