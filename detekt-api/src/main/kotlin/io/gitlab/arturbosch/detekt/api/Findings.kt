package io.gitlab.arturbosch.detekt.api

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import kotlin.reflect.full.memberProperties

/**
 * Base interface of detection findings. Inherits a bunch of useful behaviour
 * from sub interfaces.
 *
 * Basic behaviour of a finding is that is can be assigned to an id and a source code position described as
 * an entity. Metrics and entity references can also considered for deeper characterization.
 *
 * @author Artur Bosch
 */
interface Finding : Compactable, Describable, Reflective, HasEntity, HasMetrics {
	val id: String
	val references: List<Entity>
}

/**
 * Describes a source code position.
 */
interface HasEntity {
	val entity: Entity
	val location: Location
		get() = entity.location
	val locationAsString: String
		get() = location.locationString
	val startPosition: SourceLocation
		get() = location.source
	val charPosition: TextLocation
		get() = location.text
	val name: String
		get() = entity.name
	val inClass: String
		get() = entity.className
	val signature: String
		get() = entity.signature
}

/**
 * Adds metric container behaviour.
 */
interface HasMetrics {
	val metrics: List<Metric>
	fun metricByType(type: String): Metric? = metrics.find { it.type == type }
}

/**
 * Allows to iterate over attributes reflectively.
 */
interface Reflective {
	fun findAttribute(name: String): Any? {
		return this.javaClass.kotlin.memberProperties.find { it.name == name }?.get(this)
	}
}

/**
 * Provides a compact string representation.
 */
interface Compactable {
	fun compact(): String
	fun compactWithSignature(): String = compact()
}

/**
 * Provides a description attribute.
 */
interface Describable {
	val description: String
}

open class CodeSmellWithReferenceAndMetric(
		id: String, entity: Entity, val reference: Entity, metric: Metric) : ThresholdedCodeSmell(
		id, entity, metric, references = listOf(reference)) {

	override fun compact(): String {
		return "$id - $metric - ref=${reference.name} - ${entity.compact()}"
	}
}

/**
 * Represents a code smell for which a specific metric can be determined which is responsible
 * for the existence of a rule violation.
 */
open class ThresholdedCodeSmell(
		id: String, entity: Entity, val metric: Metric, references: List<Entity> = emptyList()) : CodeSmell(
		id, entity, metrics = listOf(metric), references = references) {
	val value: Int
		get() = metric.value
	val threshold: Int
		get() = metric.threshold

	override fun compact(): String {
		return "$id - $metric - ${entity.compact()}"
	}
}

/**
 * A code smell is a finding, implementing its behaviour. Use this class to store
 * rule violations.
 */
open class CodeSmell(override val id: String,
					 override val entity: Entity,
					 override val description: String = "",
					 override val metrics: List<Metric> = listOf(),
					 override val references: List<Entity> = listOf()) : Finding {

	override fun compact(): String {
		return "$id - ${entity.compact()}"
	}

	override fun compactWithSignature(): String {
		return compact() + " - Signature=" + entity.signature
	}
}

/**
 * Metric type, can be an integer or double value. Internally it is stored as an integer,
 * but the conversion factor and is double attributes can be used to retrieve it as a double value.
 */
data class Metric(val type: String,
				  val value: Int,
				  val threshold: Int,
				  val isDouble: Boolean = false,
				  val conversionFactor: Int = 100) {

	constructor(type: String,
				value: Double,
				threshold: Double,
				conversionFactor: Int) : this(type, value = (value * conversionFactor).toInt(),
			threshold = (threshold * conversionFactor).toInt(),
			isDouble = true, conversionFactor = conversionFactor)

	fun doubleValue(): Double = value.convertAsDouble()
	fun doubleThreshold(): Double = threshold.convertAsDouble()

	private fun Int.convertAsDouble() = if (isDouble) (this.toDouble() / conversionFactor)
	else throw IllegalStateException("This metric was not marked as double!")

	override fun toString(): String {
		return if (isDouble) "${doubleValue()}/${doubleThreshold()}" else "$value/$threshold"
	}

}

/**
 * Specifies a position within a source code fragment.
 */
data class Location(val source: SourceLocation,
					val text: TextLocation,
					val locationString: String,
					val file: String) : Compactable {

	override fun compact(): String {
		return "Line/Column=$source - Path=$file"
	}

	companion object {
		fun from(element: PsiElement, offset: Int = 0): Location {
			val start = startLineAndColumn(element, offset)
			val sourceLocation = SourceLocation(start.line, start.column)
			val textLocation = TextLocation(element.startOffset + offset, element.endOffset + offset)
			val fileName = element.originalFilePath() ?: element.containingFile.name
			val locationText = withPsiTextRuntimeError({ element.searchName() }) {
				element.getTextWithLocation()
			}
			return Location(sourceLocation, textLocation, locationText, fileName)
		}

		fun startLineAndColumn(element: PsiElement, offset: Int = 0): DiagnosticUtils.LineAndColumn {
			val range = element.textRange
			return DiagnosticUtils.getLineAndColumnInPsiFile(element.containingFile,
					TextRange(range.startOffset + offset, range.endOffset + offset))
		}

		private fun PsiElement.originalFilePath(): String? {
			return (this.containingFile.viewProvider.virtualFile as LightVirtualFile).originalFile?.name
		}
	}
}

/**
 * Stores information about a specific code fragment.
 */
data class Entity(val name: String,
				  val className: String,
				  val signature: String,
				  val location: Location,
				  val ktElement: KtElement? = null) : Compactable {

	override fun compact(): String {
		return "[$name] - ${location.compact()}"
	}

	companion object {
		fun from(element: PsiElement, offset: Int = 0): Entity {
			val name = element.searchName()
			val signature = element.buildFullSignature()
			val clazz = element.searchClass()
			return Entity(name, clazz, signature, Location.from(element, offset),
					if (element is KtElement) element else null)
		}
	}

}

/**
 * Stores line and column information of a location.
 */
data class SourceLocation(val line: Int, val column: Int) {
	override fun toString(): String {
		return "($line,$column)"
	}
}

/**
 * Stores character start and end positions of an text file.
 */
data class TextLocation(val start: Int, val end: Int) {
	override fun toString(): String {
		return "($start,$end)"
	}
}