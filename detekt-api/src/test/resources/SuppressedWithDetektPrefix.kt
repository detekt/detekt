@Suppress("Detekt.ALL")
object SuppressedWithDetektPrefix {

	fun stuff() {
		println("FAILED TEST")
	}
}

@Suppress("detekt:ALL")
object SuppressedWithAnotherDetektPrefix {

	fun stuff() {
		println("FAILED TEST")
	}
}

@Suppress("DETEKT:Test")
object AlsoSuppressedByDetektPrefix {

	fun stuff() {
		println("FAILED TEST")
	}
}
