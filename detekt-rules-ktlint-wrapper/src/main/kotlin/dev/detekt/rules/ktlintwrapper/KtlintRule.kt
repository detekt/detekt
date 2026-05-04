package dev.detekt.rules.ktlintwrapper

import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.CODE_STYLE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfig
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_STYLE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.createRuleExecutionEditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.StandardRule
import com.pinterest.ktlint.ruleset.standard.rules.MAX_LINE_LENGTH_RULE_ID
import dev.detekt.api.Config
import dev.detekt.api.Rule
import org.ec4j.core.model.Property
import org.jetbrains.kotlin.psi.KtFile

/**
 * Rule to detect formatting violations.
 *
 * Subclasses delegate the actual ktlint dispatch to [KtlintEngine]; each rule's [visit] simply
 * consumes the findings the engine collected during the shared per-file walk.
 */
abstract class KtlintRule(config: Config, description: String) : Rule(config, description) {

    abstract val wrapping: StandardRule

    internal val autocorrectDecision: AutocorrectDecision
        get() = if (autoCorrect) AutocorrectDecision.ALLOW_AUTOCORRECT else AutocorrectDecision.NO_AUTOCORRECT

    protected val codeStyle: String
        get() = config.valueOrNull("code_style")
            ?: config.parent?.let { KtlintWrapperProvider.code_style.value(it) }
            ?: KtlintWrapperProvider.code_style.defaultValue

    /**
     * Set the first time [visit] is called. The engine treats a rule as "active for this run"
     * when this flag is true OR the rule's config has `active: true`. The flag covers two cases
     * the config check alone misses: tests that instantiate a rule with [Config.empty] and call
     * [dev.detekt.test.lint] directly (config.active is false but the rule is clearly being
     * exercised), and a defensive guard against stale provider-registered instances participating
     * in the shared dispatch.
     */
    @Volatile
    internal var triggeredVisit: Boolean = false
        private set

    /**
     * Factory for fresh per-file [StandardRule] instances. [KtlintEngine] uses this so each
     * Kotlin file gets its own ktlint rule instance, preventing cross-thread races on the
     * wrapping's internal per-file state (counters, last-token, etc.). The default reflective
     * implementation works for the standard ktlint rule set; override if the wrapped
     * [StandardRule] has a non-default constructor.
     */
    open fun newWrapping(): StandardRule = wrapping::class.java.getDeclaredConstructor().newInstance()

    override fun visit(root: KtFile) {
        if (!triggeredVisit) {
            // Self-register so the engine sees this exact instance — and its config — instead of
            // any stale registration with the same RuleId. KtlintWrapperProvider also registers
            // rules at construction time, but tests instantiate rules outside the provider.
            KtlintEngine.register(this)
            triggeredVisit = true
        }
        val context = KtlintEngine.contextFor(root)
        try {
            context.findings[wrapping.ruleId]?.forEach(::report)
        } finally {
            KtlintEngine.ruleDoneWithFile(root, context)
        }
    }

    open fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String>? = null

    internal fun computeEditorConfigProperties(): EditorConfig {
        val usesEditorConfigProperties = overrideEditorConfigProperties()?.toMutableMap()
            ?: mutableMapOf()

        /* Notify ktlint that max line length rule is enabled so the max line length configuration for each rule is
           respected: https://github.com/pinterest/ktlint/pull/2783 */
        usesEditorConfigProperties[MAX_LINE_LENGTH_RULE_ID.createRuleExecutionEditorConfigProperty()] = "enabled"

        usesEditorConfigProperties[CODE_STYLE_PROPERTY] = codeStyle

        usesEditorConfigProperties[INDENT_STYLE_PROPERTY] = usesEditorConfigProperties.getOrDefault(
            INDENT_STYLE_PROPERTY,
            "space",
        )

        val properties = buildMap {
            usesEditorConfigProperties.forEach { (editorConfigProperty, defaultValue) ->
                put(
                    key = editorConfigProperty.name,
                    value = Property.builder()
                        .name(editorConfigProperty.type.name)
                        .type(editorConfigProperty.type)
                        .value(defaultValue)
                        .build()
                )
            }
        }

        return EditorConfig(properties)
    }
}
