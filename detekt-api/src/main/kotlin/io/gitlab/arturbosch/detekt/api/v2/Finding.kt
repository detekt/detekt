package io.gitlab.arturbosch.detekt.api.v2

import io.github.detekt.psi.FilePath
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.SeverityLevel
import org.jetbrains.kotlin.psi.KtElement

interface Finding {
    val id: String
    val message: String
    val location: Location
    val severityLevel: SeverityLevel
    val debt: Debt
    val ktElement: KtElement
    val autoCorrectable: Boolean
    val rule: RuleInfo
}

interface RuleInfo {
    val id: String
    val ruleSetId: String
    val description: String
}

interface Location {
    val source: SourceLocation
    val text: TextLocation
    val filePath: FilePath // TODO The api should not depend on io.github.detekt.psi.FilePath
}

interface SourceLocation {
    val line: Int
    val column: Int?

    operator fun component1(): Int = line
    operator fun component2(): Int? = column

    companion object {
        operator fun invoke(line: Int, column: Int?): SourceLocation {
            return Impl(line, column)
        }
    }

    private data class Impl(
        override val line: Int,
        override val column: Int?
    ) : SourceLocation {
        override fun toString(): String = "$line:$column"
    }
}

interface TextLocation {
    val start: Int
    val end: Int

    operator fun component1(): Int = start
    operator fun component2(): Int = end

    companion object {
        operator fun invoke(start: Int, end: Int): TextLocation {
            return Impl(start, end)
        }
    }

    private data class Impl(
        override val start: Int,
        override val end: Int
    ) : TextLocation {
        override fun toString(): String = "$start:$end"
    }
}
