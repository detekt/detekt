package dev.detekt.api

import dev.detekt.api.internal.validateIdentifier
import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import java.net.URI

/**
 * A rule defines how one specific code structure should look like. If code is found
 * which does not meet this structure, it is considered as harmful regarding maintainability
 * or readability.
 *
 * A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
 * function. If calculations must be done before or after the visiting process, here are
 * two predefined (preVisit/postVisit) functions which can be overridden to setup/teardown additional data.
 *
 * @property url An url pointing to the documentation of this rule
 */
open class Rule(val config: Config, val description: String, val url: URI? = null) : DetektVisitor() {

    /**
     * An id this rule is identified with.
     *
     * By default, it is the name of the class name. Override to change it.
     */
    open val ruleName: RuleName get() = RuleName(javaClass.simpleName)

    protected lateinit var languageVersionSettings: LanguageVersionSettings

    /**
     * Whether this rule should correct the code it reports on.
     *
     * Only rules implementing [AutoCorrectable] can correct anything, so for every other rule this
     * is always `false`. For those that can, the value is resolved in priority order:
     * - the `autoCorrect` property of the rule
     * - the `autoCorrect` property of the parent rule set
     * - `true`, as a rule which is able to correct is expected to do so
     *
     * The `--auto-correct` CLI flag (and its Gradle counterpart) remains the master switch: when it
     * is off, detekt forces `autoCorrect` to `false` for every rule regardless of configuration.
     */
    val autoCorrect: Boolean
        get() = this is AutoCorrectable &&
            (
                config.valueOrNull<Boolean>(Config.AUTO_CORRECT_KEY)
                    ?: config.parent?.valueOrNull<Boolean>(Config.AUTO_CORRECT_KEY)
                    ?: true
                )

    private val findings: MutableList<Finding> = mutableListOf()

    /**
     * Before starting visiting kotlin elements, a check is performed if this rule should be triggered.
     * Pre- and post-visit-hooks are executed before/after the visiting process.
     */
    fun visitFile(root: KtFile, languageVersionSettings: LanguageVersionSettings): List<Finding> {
        findings.clear()
        this.languageVersionSettings = languageVersionSettings
        preVisit(root)
        visit(root)
        postVisit(root)
        return findings
    }

    /**
     * Could be overridden by subclasses to specify a behaviour which should be done before
     * visiting kotlin elements.
     */
    protected open fun preVisit(root: KtFile) {
        // nothing to do by default
    }

    /**
     * Init function to start visiting the [KtFile].
     * Can be overridden to start a different visiting process.
     */
    open fun visit(root: KtFile) {
        root.accept(this)
    }

    /**
     * Could be overridden by subclasses to specify a behaviour which should be done after
     * visiting kotlin elements.
     */
    protected open fun postVisit(root: KtFile) {
        // nothing to do by default
    }

    /**
     * Adds a new finding
     */
    fun report(finding: Finding) {
        findings.add(finding)
    }
}

@Poko
class RuleName(val value: String) {
    init {
        validateIdentifier(value)
    }

    override fun toString(): String = value
}
