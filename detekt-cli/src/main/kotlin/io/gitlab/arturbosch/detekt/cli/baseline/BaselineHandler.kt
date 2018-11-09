package io.gitlab.arturbosch.detekt.cli.baseline

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.time.Instant

/**
 * @author Artur Bosch
 */
internal class BaselineHandler : DefaultHandler() {

	private var current: String? = null
	private var content: String = ""
	private var whitestamp: String? = null
	private var blackstamp: String? = null
	private var sourceSetId: String? = null

	private val currentBaselines = mutableListOf<Baseline>()
	private val sourceSetIds = mutableSetOf<String?>()
	private val whiteIds = mutableSetOf<String>()
	private val blackIds = mutableSetOf<String>()

	private val now = Instant.now().toEpochMilli().toString()

	internal fun createConsolidatedBaseline() = ConsolidatedBaseline(currentBaselines.toList())

	internal fun createBaseline(sourceSetId: String?): Baseline {
		return currentBaselines.firstOrNull { it.sourceSetId == sourceSetId }
				?: Baseline(sourceSetId, Blacklist(emptySet(), now), Whitelist(emptySet(), now))
	}

	override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
		when (qName) {
			SMELL_BASELINE -> {
				sourceSetId = attributes.getValue(SOURCE_SET_ID)
				if (sourceSetIds.contains(sourceSetId)) {
					val msg = if (sourceSetId == null)
						"There are multiple whitelists not associated to a source set"
					else "There are multiple whitelists associated to source set $sourceSetId"
					throw InvalidBaselineState(msg)
				}
				sourceSetIds.add(sourceSetId)
				current = null
			}
			BLACKLIST -> {
				current = BLACKLIST
				blackstamp = attributes.getValue(TIMESTAMP)
			}
			WHITELIST -> {
				current = WHITELIST
				whitestamp = attributes.getValue(TIMESTAMP)
			}
			ID -> content = ""
		}
	}

	override fun endElement(uri: String, localName: String, qName: String) {
		when (qName) {
			SMELL_BASELINE -> {
				currentBaselines.add(Baseline(
						sourceSetId,
						Blacklist(blackIds.toSet(), blackstamp ?: now),
						Whitelist(whiteIds.toSet(), whitestamp ?: now)
				))
				blackIds.clear()
				whiteIds.clear()
				blackstamp = null
				whitestamp = null
			}
			ID -> if (content.isNotBlank()) {
				when (current) {
					BLACKLIST -> blackIds.add(content)
					WHITELIST -> whiteIds.add(content)
				}
				content = ""
			}
			BLACKLIST, WHITELIST -> current == null
		}
	}

	override fun characters(ch: CharArray, start: Int, length: Int) {
		if (current != null) content += String(ch, start, length)
	}
}
