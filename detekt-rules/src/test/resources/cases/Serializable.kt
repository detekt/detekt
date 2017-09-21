@file:Suppress("unused")

package cases

import java.io.Serializable

class IncorrectSerialVersionUID1 : Serializable {
	companion object {
		const val serialVersionUID = 1 // violation
	}
}

class IncorrectSerialVersionUID2 : Serializable {
	companion object {
		const val serialVersionUUID = 1L // violation: naming
	}

	object NestedIncorrectSerialVersionUID : Serializable {
		val serialVersionUUID = 1L // violation
	}
}

class IncorrectSerialVersionUID3 : Serializable {
	companion object {
		const val serialVersionUID: Int = 1 // violation
	}
}

class NoSerialVersionUID : Serializable // violation

class CorrectSerializable1 : Serializable {
	companion object {
		const val serialVersionUID = 1L
	}
}

class CorrectSerializable2 : Serializable {
	companion object {
		const val serialVersionUID: Long = 1
	}
}

class NoSerializableClass

interface SerializableInterface : Serializable
