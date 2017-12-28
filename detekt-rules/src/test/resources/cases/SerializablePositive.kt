@file:Suppress("unused")

package cases

import java.io.Serializable

class IncorrectSerialVersionUID1 : Serializable {
	companion object {
		const val serialVersionUID = 1 // reports 1 - wrong datatype
	}
}

class IncorrectSerialVersionUID2 : Serializable {
	companion object {
		const val serialVersionUUID = 1L // reports 1 - wrong naming
	}

	object NestedIncorrectSerialVersionUID : Serializable {
		val serialVersionUUID = 1L // reports 1 - missing const modifier
	}
}

class IncorrectSerialVersionUID3 : Serializable {
	companion object {
		const val serialVersionUID: Int = 1 // reports 1 - wrong datatype
	}
}

class NoSerialVersionUID : Serializable // reports 1 - no serialVersionUID at all
