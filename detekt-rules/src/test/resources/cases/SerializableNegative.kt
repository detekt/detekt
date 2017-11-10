@file:Suppress("unused")

package cases

import java.io.Serializable

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
