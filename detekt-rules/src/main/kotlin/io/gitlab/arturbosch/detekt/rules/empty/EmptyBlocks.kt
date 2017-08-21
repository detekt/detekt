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
	private val emptySecondaryConstructorBlock = EmptySecondaryConstructorBlock(config)
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
			emptySecondaryConstructorBlock,
			emptyWhenBlock,
			emptyWhileBlock
	)

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		report(rules.flatMap { it.findings })
	}

	override fun visitCatchSection(catchClause: KtCatchClause) {
		if (emptyCatchBlock.active) {
			emptyCatchBlock.visitCatchSection(catchClause)
		}
		super.visitCatchSection(catchClause)
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (emptyClassBlock.active) {
			emptyClassBlock.visitClassOrObject(classOrObject)
		}
		super.visitClassOrObject(classOrObject)
	}

	override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
		if (emptyDefaultConstructor.active) {
			emptyDefaultConstructor.visitPrimaryConstructor(constructor)
		}
		super.visitPrimaryConstructor(constructor)
	}

	override fun visitDoWhileExpression(expression: KtDoWhileExpression) {
		if (emptyDoWhileBlock.active) {
			emptyDoWhileBlock.visitDoWhileExpression(expression)
		}
		super.visitDoWhileExpression(expression)
	}

	override fun visitIfExpression(expression: KtIfExpression) {
		if (emptyIfBlock.active) {
			emptyIfBlock.visitIfExpression(expression)
		}
		if (emptyElseBlock.active) {
			emptyElseBlock.visitIfExpression(expression)
		}
		super.visitIfExpression(expression)
	}

	override fun visitFinallySection(finallySection: KtFinallySection) {
		if (emptyFinallyBlock.active) {
			emptyFinallyBlock.visitFinallySection(finallySection)
		}
		super.visitFinallySection(finallySection)
	}

	override fun visitForExpression(expression: KtForExpression) {
		if (emptyForBlock.active) {
			emptyForBlock.visitForExpression(expression)
		}
		super.visitForExpression(expression)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (emptyFunctionBlock.active) {
			emptyFinallyBlock.visitNamedFunction(function)
		}
		super.visitNamedFunction(function)
	}

	override fun visitClassInitializer(initializer: KtClassInitializer) {
		if (emptyInitBlock.active) {
			emptyInitBlock.visitClassInitializer(initializer)
		}
		super.visitClassInitializer(initializer)
	}

	override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
		if (emptySecondaryConstructorBlock.active) {
			emptySecondaryConstructorBlock.visitSecondaryConstructor(constructor)
		}
		super.visitSecondaryConstructor(constructor)
	}

	override fun visitWhenExpression(expression: KtWhenExpression) {
		if (emptyWhenBlock.active) {
			emptyWhenBlock.visitWhenExpression(expression)
		}
		super.visitWhenExpression(expression)
	}

	override fun visitWhileExpression(expression: KtWhileExpression) {
		if (emptyWhileBlock.active) {
			emptyWhileBlock.visitWhileExpression(expression)
		}
		super.visitWhileExpression(expression)
	}
}
