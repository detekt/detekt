package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.test.utils.NullPrintStream
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class PathFiltersSpec {

    @Test
    fun `should load single filter`() {
        val filters = CliArgs { excludes = "**/one/**" }.toSpecFilters()
        assertThat(filters?.isIgnored(Path("/one/path"))).isTrue()
        assertThat(filters?.isIgnored(Path("/two/path"))).isFalse()
    }

    @Nested
    inner class `parsing with different nullability combinations of path filters` {
        @Test
        fun `returns an empty path filter when includes are empty and excludes are empty`() {
            val pathFilter = PathFilters.of(emptyList(), emptyList())
            assertThat(pathFilter).isNull()
        }

        @Test
        fun `parses includes correctly`() {
            val pathFilter = PathFilters.of(listOf("**/one/**", "**/two/**"), emptyList())
            assertThat(pathFilter).isNotNull
            assertThat(pathFilter?.isIgnored(Path("/one/path"))).isFalse
            assertThat(pathFilter?.isIgnored(Path("/two/path"))).isFalse
            assertThat(pathFilter?.isIgnored(Path("/three/path"))).isTrue
        }

        @Test
        fun `parses excludes correctly`() {
            val pathFilter = PathFilters.of(emptyList(), listOf("**/one/**", "**/two/**"))
            assertThat(pathFilter).isNotNull
            assertThat(pathFilter?.isIgnored(Path("/one/path"))).isTrue
            assertThat(pathFilter?.isIgnored(Path("/two/path"))).isTrue
            assertThat(pathFilter?.isIgnored(Path("/three/path"))).isFalse
        }

        @Test
        fun `parses both includes and excludes correctly`() {
            val pathFilter = PathFilters.of(listOf("**/one/**"), listOf("**/two/**"))
            assertThat(pathFilter).isNotNull
            assertThat(pathFilter?.isIgnored(Path("/one/path"))).isFalse
            assertThat(pathFilter?.isIgnored(Path("/two/path"))).isTrue
            assertThat(pathFilter?.isIgnored(Path("/three/path"))).isTrue
            assertThat(pathFilter?.isIgnored(Path("/one/two/three/path"))).isTrue
        }
    }

    @Nested
    inner class `parsing with different separators` {

        @Test
        fun `should load multiple comma-separated filters with no spaces around commas`() {
            val filters = CliArgs { excludes = "**/one/**,**/two/**,**/three" }.toSpecFilters()
            assertSameFiltersIndependentOfSpacingAndSeparater(filters)
        }

        @Test
        fun `should load multiple semicolon-separated filters with no spaces around semicolons`() {
            val filters = CliArgs { excludes = "**/one/**;**/two/**;**/three" }.toSpecFilters()
            assertSameFiltersIndependentOfSpacingAndSeparater(filters)
        }

        @Test
        fun `should load multiple comma-separated filters with spaces around commas`() {
            val filters = CliArgs { excludes = "**/one/** ,**/two/**, **/three" }.toSpecFilters()
            assertSameFiltersIndependentOfSpacingAndSeparater(filters)
        }

        @Test
        fun `should load multiple semicolon-separated filters with spaces around semicolons`() {
            val filters = CliArgs { excludes = "**/one/** ;**/two/**; **/three" }.toSpecFilters()
            assertSameFiltersIndependentOfSpacingAndSeparater(filters)
        }

        @Test
        fun `should load multiple mixed-separated filters with no spaces around separators`() {
            val filters = CliArgs { excludes = "**/one/**,**/two/**;**/three" }.toSpecFilters()
            assertSameFiltersIndependentOfSpacingAndSeparater(filters)
        }

        @Test
        fun `should load multiple mixed-separated filters with spaces around separators`() {
            val filters = CliArgs { excludes = "**/one/** ,**/two/**; **/three" }.toSpecFilters()
            assertSameFiltersIndependentOfSpacingAndSeparater(filters)
        }
    }

    @Test
    fun `should ignore empty and blank filters`() {
        val filters = CliArgs { excludes = " ,,**/three" }.toSpecFilters()
        assertThat(filters?.isIgnored(Path("/three"))).isTrue()
        assertThat(filters?.isIgnored(Path("/root/three"))).isTrue()
        assertThat(filters?.isIgnored(Path("/one/path"))).isFalse()
        assertThat(filters?.isIgnored(Path("/two/path"))).isFalse()
        assertThat(filters?.isIgnored(Path("/three/path"))).isFalse()
    }
}

private fun CliArgs.toSpecFilters(): PathFilters? {
    val spec = this.createSpec(NullPrintStream(), NullPrintStream()).projectSpec
    return PathFilters.of(spec.includes.toList(), spec.excludes.toList())
}

// can parse pattern **/one/**,**/two/**,**/three
private fun assertSameFiltersIndependentOfSpacingAndSeparater(filters: PathFilters?) {
    assertThat(filters?.isIgnored(Path("/one/path"))).isTrue()
    assertThat(filters?.isIgnored(Path("/two/path"))).isTrue()
    assertThat(filters?.isIgnored(Path("/three"))).isTrue()
    assertThat(filters?.isIgnored(Path("/root/three"))).isTrue()
    assertThat(filters?.isIgnored(Path("/three/path"))).isFalse()
}
