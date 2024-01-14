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
}

private fun CliArgs.toSpecFilters(): PathFilters? {
    val spec = this.createSpec(NullPrintStream(), NullPrintStream()).projectSpec
    return PathFilters.of(spec.includes.toList(), spec.excludes.toList())
}
