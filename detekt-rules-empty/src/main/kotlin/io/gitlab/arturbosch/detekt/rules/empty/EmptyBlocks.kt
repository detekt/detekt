package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 *
 * <noncompliant>
 * // unnecessary empty blocks should be removed
 * fun unnecessaryFunction() {
 * }
 * </noncompliant>
 *
 * @active since v1.0.0
 */
@Suppress("TooManyFunctions")
class EmptyBlocks(val config: Config = Config.empty) : MultiRule() {

    private val emptyCatchBlock = EmptyCatchBlock(config)
    private val emptyClassBlock = EmptyClassBlock(config)
    private val emptyDefaultConstructor = EmptyDefaultConstructor(config)
    private val emptyDoWhileBlock = EmptyDoWhileBlock(config)
    private val emptyElseBlock = EmptyElseBlock(config)
    private val emptyFinallyBlock = EmptyFinallyBlock(config)
    private val emptyForBlock = EmptyForBlock(config)
    private val emptyFunctionBlock = EmptyFunctionBlock(config)
    private val emptyIfBlock = EmptyIfBlock(config)
    private val emptyInitBlock = EmptyInitBlock(config)
    private val emptyKtFile = EmptyKtFile(config)
    private val emptySecondaryConstructorBlock = EmptySecondaryConstructor(config)
    private val emptyTryBlock = EmptyTryBlock(config)
    private val emptyWhenBlock = EmptyWhenBlock(config)
    private val emptyWhileBlock = EmptyWhileBlock(config)

    override val rules: List<Rule> = listOf(
            emptyCatchBlock,
            emptyClassBlock,
            emptyDefaultConstructor,
            emptyDoWhileBlock,
            emptyElseBlock,
            emptyFinallyBlock,
            emptyForBlock,
            emptyFunctionBlock,
            emptyIfBlock,
            emptyInitBlock,
            emptyKtFile,
            emptySecondaryConstructorBlock,
            emptyTryBlock,
            emptyWhenBlock,
            emptyWhileBlock
    )

    override fun visitKtFile(file: KtFile) {
        emptyKtFile.runIfActive { visitFile(file) }
        emptyClassBlock.runIfActive {
            file.declarations.filterIsInstance<KtClassOrObject>().forEach {
                visitClassOrObject(it)
            }
        }
        super.visitKtFile(file)
    }

    override fun visitTryExpression(expression: KtTryExpression) {
        emptyTryBlock.runIfActive { visitTryExpression(expression) }
        super.visitTryExpression(expression)
    }

    override fun visitCatchSection(catchClause: KtCatchClause) {
        emptyCatchBlock.runIfActive { visitCatchSection(catchClause) }
        super.visitCatchSection(catchClause)
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        emptyDefaultConstructor.runIfActive { visitPrimaryConstructor(constructor) }
        super.visitPrimaryConstructor(constructor)
    }

    override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
        emptyDoWhileBlock.runIfActive { visitDoWhileExpression(expression) }
        super.visitDoWhileExpression(expression)
    }

    override fun visitIfExpression(expression: KtIfExpression) {
        emptyIfBlock.runIfActive { visitIfExpression(expression) }
        emptyElseBlock.runIfActive { visitIfExpression(expression) }
        super.visitIfExpression(expression)
    }

    override fun visitFinallySection(finallySection: KtFinallySection) {
        emptyFinallyBlock.runIfActive { visitFinallySection(finallySection) }
        super.visitFinallySection(finallySection)
    }

    override fun visitForExpression(expression: KtForExpression) {
        emptyForBlock.runIfActive { visitForExpression(expression) }
        super.visitForExpression(expression)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        emptyFunctionBlock.runIfActive { visitNamedFunction(function) }
        super.visitNamedFunction(function)
    }

    override fun visitClassInitializer(initializer: KtClassInitializer) {
        emptyInitBlock.runIfActive { visitClassInitializer(initializer) }
        super.visitClassInitializer(initializer)
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        emptySecondaryConstructorBlock.runIfActive { visitSecondaryConstructor(constructor) }
        super.visitSecondaryConstructor(constructor)
    }

    override fun visitWhenExpression(expression: KtWhenExpression) {
        emptyWhenBlock.runIfActive { visitWhenExpression(expression) }
        super.visitWhenExpression(expression)
    }

    override fun visitWhileExpression(expression: KtWhileExpression) {
        emptyWhileBlock.runIfActive { visitWhileExpression(expression) }
        super.visitWhileExpression(expression)
    }
}
