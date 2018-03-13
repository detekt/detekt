@file:Suppress("unused", "RedundantOverride")

package cases

class DataClassCandidate1(val i: Int) // reports 1

class DataClassCandidateWithProperties(val i: Int) { // reports 1

	val i2: Int = 0
}

class DataClassCandidate2(val s: String) { // reports 1 - also has a public constructor

	private constructor(i: Int) : this(i.toString())
}

class DataClassCandidate3 private constructor(val s: String) { // reports 1 - also has a public constructor

	constructor(i: Int) : this(i.toString())
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
