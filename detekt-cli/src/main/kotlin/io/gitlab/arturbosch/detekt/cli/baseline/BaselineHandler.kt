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
	private var currentSourceSetId: String? = null

	private val currentWhitelists = mutableListOf<Whitelist>()
	private val currentWhiteIds = mutableSetOf<String>()
	private val blackIds = mutableSetOf<String>()

	internal fun createConsolidatedBaseline() = ConsolidatedBaseline(
			Blacklist(blackIds, blackstamp ?: now()),
			whitelistIdentifiedBy(null),
			sourceSetWhitelistsAsMap()
	)

	private fun sourceSetWhitelistsAsMap(): Map<String, Whitelist> {
		val sourceSets = currentWhitelists.map(Whitelist::sourceSetId)

		val result = mutableMapOf<String, Whitelist>()
		// by iterating over the ids we also check for duplicates
		sourceSets.forEach {
			it?.let { sourceSetId ->
				val whitelist = whitelistIdentifiedBy(sourceSetId)
				if (whitelist != null) {
					result.put(sourceSetId, whitelist)
				}
			}
		}

		return result
	}

	internal fun createBaseline(sourceSetId: String?) = Baseline(
			Blacklist(blackIds, blackstamp ?: now()),
			whitelistIdentifiedBy(sourceSetId) ?: Whitelist(sourceSetId, emptySet(), whitestamp ?: now())
	)

	private fun now() = Instant.now().toEpochMilli().toString()

	private fun whitelistIdentifiedBy(sourceSetId: String?): Whitelist? {
		val matching = currentWhitelists.filter { it.sourceSetId == sourceSetId }
		if (matching.size > 1) {
			val msg = if (sourceSetId == null)
				"There are multiple whitelists not associated to a source set"
			else "There are multiple whitelists associated to source set $sourceSetId"
			throw InvalidBaselineState(msg)
		}
		return matching.firstOrNull()
	}

	override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
		when (qName) {
			BLACKLIST -> {
				current = BLACKLIST
				blackstamp = attributes.getValue(TIMESTAMP)
			}
			WHITELIST -> {
				current = WHITELIST
				currentSourceSetId = attributes.getValue(SOURCE_SET_ID)
				whitestamp = attributes.getValue(TIMESTAMP)
			}
			ID -> content = ""
		}
	}

	override fun endElement(uri: String, localName: String, qName: String) {
		when (qName) {
			ID -> if (content.isNotBlank()) {
				when (current) {
					BLACKLIST -> blackIds.add(content)
					WHITELIST -> currentWhiteIds.add(content)
				}
				content = ""
			}
			BLACKLIST -> current == null
			WHITELIST -> {
				currentWhitelists.add(Whitelist(currentSourceSetId, currentWhiteIds.toSet(), whitestamp ?: now()))
				current = null
				currentSourceSetId = null
				currentWhiteIds.clear()
			}

		}
	}

	override fun characters(ch: CharArray, start: Int, length: Int) {
		if (current != null) content += String(ch, start, length)
	}
}
