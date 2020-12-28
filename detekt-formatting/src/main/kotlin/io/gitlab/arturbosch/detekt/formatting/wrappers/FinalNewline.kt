package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.ruleset.standard.FinalNewlineRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.INSERT_FINAL_NEWLINE_KEY
import org.ec4j.core.model.Property
import org.ec4j.core.model.PropertyType

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @configuration insertFinalNewLine - report absence or presence of a newline (default: `true`)
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 *
 */
@OptIn(FeatureInAlphaState::class)
class FinalNewline(config: Config) : FormattingRule(config) {

    override val wrapping = FinalNewlineRule()
    override val issue = issueFor("Detects missing final newlines")

    private val insertFinalNewline = valueOrDefault(INSERT_FINAL_NEWLINE, true)

    // HACK! FinalNewlineRule.insertNewLineProperty is internal. Therefore we are building
    // our custom Property to override the editor config properties.
    // When FinalNewlineRule exits the alpha/beta state, hopefully we could remove this hack.
    override fun overrideEditorConfigProperties() = mapOf(
        INSERT_FINAL_NEWLINE_KEY to
            Property.builder().type(PropertyType.insert_final_newline).value(insertFinalNewline.toString()).build()
    )
}

const val INSERT_FINAL_NEWLINE = "insertFinalNewline"
