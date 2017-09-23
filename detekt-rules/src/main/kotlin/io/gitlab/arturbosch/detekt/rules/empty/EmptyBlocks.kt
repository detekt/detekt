package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.SingleAssign
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
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * @author Artur Bosch
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
			emptyWhenBlock,
			emptyWhileBlock
	)

	private var activeRules: Set<Rule> by SingleAssign()

	override fun visitKtFile(file: KtFile) {
		activeRules = rules.filterTo(HashSet()) { it.visitCondition(file) }
		if (emptyKtFile in activeRules) {
			emptyKtFile.visitFile(file)
		}
		super.visitKtFile(file)
		report(activeRules.flatMap { it.findings })
	}

	override fun visitCatchSection(catchClause: KtCatchClause) {
		if (emptyCatchBlock in activeRules) {
			emptyCatchBlock.visitCatchSection(catchClause)
		}
		super.visitCatchSection(catchClause)
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (emptyClassBlock in activeRules) {
			emptyClassBlock.visitClassOrObject(classOrObject)
		}
		super.visitClassOrObject(classOrObject)
	}

	override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
		if (emptyDefaultConstructor in activeRules) {
			emptyDefaultConstructor.visitPrimaryConstructor(constructor)
		}
		super.visitPrimaryConstructor(constructor)
	}

	override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
		if (emptyDoWhileBlock in activeRules) {
			emptyDoWhileBlock.visitDoWhileExpression(expression)
		}
		super.visitDoWhileExpression(expression)
	}

	override fun visitIfExpression(expression: KtIfExpression) {
		if (emptyIfBlock in activeRules) {
			emptyIfBlock.visitIfExpression(expression)
		}
		if (emptyElseBlock in activeRules) {
			emptyElseBlock.visitIfExpression(expression)
		}
		super.visitIfExpression(expression)
	}

	override fun visitFinallySection(finallySection: KtFinallySection) {
		if (emptyFinallyBlock in activeRules) {
			emptyFinallyBlock.visitFinallySection(finallySection)
		}
		super.visitFinallySection(finallySection)
	}

	override fun visitForExpression(expression: KtForExpression) {
		if (emptyForBlock in activeRules) {
			emptyForBlock.visitForExpression(expression)
		}
		super.visitForExpression(expression)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (emptyFunctionBlock in activeRules) {
			emptyFinallyBlock.visitNamedFunction(function)
		}
		super.visitNamedFunction(function)
	}

	override fun visitClassInitializer(initializer: KtClassInitializer) {
		if (emptyInitBlock in activeRules) {
			emptyInitBlock.visitClassInitializer(initializer)
		}
		super.visitClassInitializer(initializer)
	}

	override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
		if (emptySecondaryConstructorBlock in activeRules) {
			emptySecondaryConstructorBlock.visitSecondaryConstructor(constructor)
		}
		super.visitSecondaryConstructor(constructor)
	}

	override fun visitWhenExpression(expression: KtWhenExpression) {
		if (emptyWhenBlock in activeRules) {
			emptyWhenBlock.visitWhenExpression(expression)
		}
		super.visitWhenExpression(expression)
	}

	override fun visitWhileExpression(expression: KtWhileExpression) {
		if (emptyWhileBlock in activeRules) {
			emptyWhileBlock.visitWhileExpression(expression)
		}
		super.visitWhileExpression(expression)
	}
}
