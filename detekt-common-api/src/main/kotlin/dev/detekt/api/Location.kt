package dev.detekt.api

import dev.drewhamilton.poko.Poko
import java.nio.file.Path

/**
 * Specifies a position within a source code fragment.
 */
class Location(val source: SourceLocation, val endSource: SourceLocation, val text: TextLocation, val path: Path) {
    override fun toString(): String = "Location(source=$source, endSource=$endSource, text=$text, path=$path)"

    companion object
}

/**
 * Stores line and column information of a location.
 */
@Poko
class SourceLocation(val line: Int, val column: Int) : Comparable<SourceLocation> {
    init {
        require(line > 0) { "The source location line must be greater than 0" }
        require(column > 0) { "The source location column must be greater than 0" }
    }

    override fun toString(): String = "$line:$column"

    override fun compareTo(other: SourceLocation): Int = compareValuesBy(this, other, { it.line }, { it.column })
}

/**
 * Stores character start and end positions of a text file.
 */
@Poko
class TextLocation(val start: Int, val end: Int) {
    init {
        require(start >= 0) { "start can't be negative" }
        require(end >= start) { "end must be greater than or equal to start" }
    }

    override fun toString(): String = "$start:$end"
}
