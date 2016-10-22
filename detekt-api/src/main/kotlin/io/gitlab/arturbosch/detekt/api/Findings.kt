package io.gitlab.arturbosch.detekt.api

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import kotlin.reflect.memberProperties

/**
 * @author Artur Bosch
 */
interface Finding : Compactable, Describable, Reflective, HasEntity, HasMetrics {
	val id: String
	val references: List<Entity>
}

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

interface HasMetrics {
	val metrics: List<Metric>
	fun metricByType(type: String): Metric? = metrics.find { it.type == type }
}

interface Reflective {
	fun findAttribute(name: String): Any? {
		return this.javaClass.kotlin.memberProperties.find { it.name == name }?.get(this)
	}
}

interface Compactable {
	fun compact(): String
}

interface Describable {
	val description: String
}

class ThresholdedCodeSmell(id: String, entity: Entity, val metric: Metric) : CodeSmell(id, entity, metrics = listOf(metric)) {
	val value: Int
		get() = metric.value
	val threshold: Int
		get() = metric.threshold

	override fun compact(): String {
		return "$id - ${metric.value}/${metric.threshold} - ${entity.compact()}"
	}
}

open class CodeSmell(override val id: String,
					 override val entity: Entity,
					 override val description: String = "",
					 override val metrics: List<Metric> = listOf(),
					 override val references: List<Entity> = listOf()) : Finding {
	override fun compact(): String {
		return "$id - ${entity.compact()}"
	}
}

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
			isDouble = true, conversionFactor = conversionFactor) {

	}

	fun doubleValue(): Double = value.convertAsDouble()
	fun doubleThreshold(): Double = threshold.convertAsDouble()

	private fun Int.convertAsDouble() = if (isDouble) (this.toDouble() / conversionFactor)
	else throw IllegalStateException("This metric was not marked as double!")
}

data class Location(val source: SourceLocation,
					val text: TextLocation,
					val locationString: String,
					val file: String) : Compactable {

	override fun compact(): String {
		return "Line/Column=$source - CharRange=$text - $file"
	}

	companion object {
		fun from(element: PsiElement): Location {
			val start = startLineAndColumn(element)
			val sourceLocation = SourceLocation(start.line, start.column)
			val textLocation = TextLocation(element.startOffset, element.endOffset)
			return Location(sourceLocation, textLocation,
					element.getTextWithLocation(), element.containingFile.name)
		}

		private fun startLineAndColumn(element: PsiElement) = DiagnosticUtils.getLineAndColumnInPsiFile(
				element.containingFile, element.textRange)
	}
}

data class Entity(val name: String,
				  val className: String,
				  val signature: String,
				  val location: Location) : Compactable {

	override fun compact(): String {
		return "[$signature] - ${location.compact()}"
	}

	companion object {
		fun from(element: PsiElement): Entity {
			val name = element.searchName()
			val signature = element.buildFullSignature()
			val clazz = element.searchClass()
			return Entity(name, clazz, signature, Location.from(element))
		}
	}

}

data class SourceLocation(val line: Int, val column: Int) {
	override fun toString(): String {
		return "($line,$column)"
	}
}

data class TextLocation(val start: Int, val end: Int) {
	override fun toString(): String {
		return "($start,$end)"
	}
}