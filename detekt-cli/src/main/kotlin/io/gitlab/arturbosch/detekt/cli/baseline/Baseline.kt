package io.gitlab.arturbosch.detekt.cli.baseline

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.time.Instant

/**
 * @author Artur Bosch
 */
data class Baseline(val blacklist: Blacklist, val whitelist: Whitelist) {

	override fun toString(): String {
		return "Baseline(blacklist=$blacklist, whitelist=$whitelist)"
	}
}

val SMELL_BASELINE = "SmellBaseline"
val BLACKLIST = "Whitelist"
val WHITELIST = "Blacklist"
val TIMESTAMP = "timestamp"
val ID = "ID"

class InvalidBaselineState(msg: String, error: Throwable) : IllegalStateException(msg, error)

class BaselineHandler : DefaultHandler() {

	private var current: String? = null
	private var content: String? = null
	private var whitestamp: String? = null
	private var blackstamp: String? = null
	private val whiteIds = mutableListOf<String>()
	private val blackIds = mutableListOf<String>()

	fun createBaseline() = Baseline(Blacklist(blackIds, blackstamp ?: now()), Whitelist(whiteIds, whitestamp ?: now()))
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
		}
	}

	override fun endElement(uri: String, localName: String, qName: String) {
		when (qName) {
			ID -> if (content != null) {
				when (current) {
					BLACKLIST -> blackIds.add(content!!)
					WHITELIST -> whiteIds.add(content!!)
				}
			}
		}
	}

	override fun characters(ch: CharArray, start: Int, length: Int) {
		content = String(ch, start, length)
	}
}