package io.gitlab.arturbosch.detekt.cli.baseline

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.time.Instant

/**
 * @author Artur Bosch
 */
class BaselineHandler : DefaultHandler() {

	private var current: String? = null
	private var content: String = ""
	private var whitestamp: String? = null
	private var blackstamp: String? = null
	private val whiteIds = mutableSetOf<String>()
	private val blackIds = mutableSetOf<String>()

	internal fun createBaseline() = Baseline(
			Blacklist(blackIds, blackstamp ?: now()), Whitelist(whiteIds, whitestamp ?: now()))

	private fun now() = Instant.now().toEpochMilli().toString()

	override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
		when (qName) {
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
