@file:Suppress("unused")

package cases

class TooGenericExceptionsOptions {

	fun f() {
		try {
			throw Throwable()
		} catch (myIgnore: MyTooGenericException) {
			throw Error()
		}
	}
}

class MyTooGenericException : RuntimeException()
