package io.gitlab.arturbosch.detekt.api

import dev.drewhamilton.poko.Poko
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * A rule defines how one specific code structure should look like. If code is found
 * which does not meet this structure, it is considered as harmful regarding maintainability
 * or readability.
 *
 * A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
 * function. If calculations must be done before or after the visiting process, here are
 * two predefined (preVisit/postVisit) functions which can be overridden to setup/teardown additional data.
 */
open class Rule(
    val config: Config,
    val description: String,
) : DetektVisitor() {

    /**
     * An id this rule is identified with.
     *
     * By default, it is the name of the class name. Override to change it.
     */
    open val ruleName: Name get() = Name(javaClass.simpleName)

    var bindingContext: BindingContext = BindingContext.EMPTY
    var compilerResources: CompilerResources? = null

    val autoCorrect: Boolean
        get() = config.valueOrDefault(Config.AUTO_CORRECT_KEY, false) &&
            (config.parent?.valueOrDefault(Config.AUTO_CORRECT_KEY, true) != false)

    private val findings: MutableList<Finding> = mutableListOf()

    /**
     * Before starting visiting kotlin elements, a check is performed if this rule should be triggered.
     * Pre- and post-visit-hooks are executed before/after the visiting process.
     * BindingContext holds the result of the semantic analysis of the source code by the Kotlin compiler. Rules that
     * rely on symbols and types being resolved can use the BindingContext for this analysis. Note that detekt must
     * receive the correct compile classpath for the code being analyzed otherwise the default value
     * [BindingContext.EMPTY] will be used and it will not be possible for detekt to resolve types or symbols.
     */
    fun visitFile(
        root: KtFile,
        bindingContext: BindingContext = BindingContext.EMPTY,
        compilerResources: CompilerResources? = null
    ): List<Finding> {
        findings.clear()
        this.bindingContext = bindingContext
        this.compilerResources = compilerResources
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
     * Adds a code smell to the findings,
     */
    fun report(finding: Finding) {
        findings.add(finding)
    }

    @Poko
    class Name(val value: String) {
        init {
            require(value.matches(nameRegex)) { "Name '$value' must match ${nameRegex.pattern}}" }
        }

        override fun toString(): String = value
    }

    @Poko
    class Id(val value: String) {
        init {
            require(value.matches(idRegex)) { "Id '$value' must match ${idRegex.pattern}" }
        }

        val ruleName: Name get() = Name(value.split("/", limit = 2).first())

        override fun toString(): String = value
    }
}

private val nameRegex = Regex("[aA-zZ]+(?:[aA-zZ0-9-]+)*[aA-zZ0-9]")
private val idRegex = Regex("${nameRegex.pattern}(/${nameRegex.pattern})?")
