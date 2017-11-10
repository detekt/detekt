@file:Suppress("unused", "RedundantOverride")

package cases

class DataClassCandidate(val i: Int) // reports 1

class DataClassCandidateWithProperties(val i: Int) { // reports 1

	val i2: Int = 0
}

class DataClassCandidateWithOverriddenMethods(val i: Int) { // reports 1

	override fun equals(other: Any?): Boolean {
		return super.equals(other)
	}

	override fun hashCode(): Int {
		return super.hashCode()
	}

	override fun toString(): String {
		return super.toString()
	}
}
