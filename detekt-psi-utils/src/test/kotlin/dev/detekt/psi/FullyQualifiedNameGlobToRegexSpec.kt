package dev.detekt.psi

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FullyQualifiedNameGlobToRegexSpec {
    @ParameterizedTest
    @CsvSource(
        "Foo,          **.*,            false",
        "Foo,          *,               true ",
        "Foo,          **,              false",
        "Foo,          ???,             true ",
        "Foo,          ??,              false",
        "Foo,          ????,            false",
        "Foo,          *?,              true ",
        "Foo,          **.Foo,          false",
        "Foo,          test.Foo,        false",
        "Foo,          F.o,             false",
        "Foo,          F?o,             true ",
        "test.Foo,     **.*,            true ",
        "test.Foo,     *,               false",
        "test.Foo,     **,              false",
        "test.Foo,     ???,             false",
        "test.Foo,     ??,              false",
        "test.Foo,     ????,            false",
        "test.Foo,     test?Foo,        false",
        "test.Foo,     *?,              false",
        "test.Foo,     **.Foo,          true ",
        "test.Foo,     test.Foo,        true ",
        "test.Foo,     F.o,             false",
        "test.Foo,     F?o,             false",
        "test.Foo,     **.F*,           true ",
        "test.bar.Foo, **.F*,           true ",
        "test.bar.Foo, **.bar.F*,       true ",
        "test.bar.Foo, test.**.F*,      true ",
        "a.b.c.d.Foo,  a.**.d.Foo,      true ",
        "a.b.c.d.Foo,  a.b.**.d.Foo,    true ",
        "a.b.c.d.Foo,  a.**.b.c.d.Foo,  false",
        "a.b.c.d.Foo,  a.**.**.c.d.Foo, false",
        "a.b.c.d.Foo,  a.**.**.c.d.Foo, false",
    )
    fun tests(fullyQualifiedName: String, glob: String, matches: Boolean) {
        assertThat(glob.fullyQualifiedNameGlobToRegex().matches(fullyQualifiedName)).isEqualTo(matches)
    }
}
