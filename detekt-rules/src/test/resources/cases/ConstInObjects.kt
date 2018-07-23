package cases

@Suppress("unused")
object Root {

	const val ROOT_CONST = 1

	object A1 {
		val ACONST = ROOT_CONST + 1 // reports 1
		const val ACONST_1 = 1

		object B1 {
			val BCONST = ACONST_1 + 1 // reports 1
		}
	}

	object A2 {
		val ACONST = ROOT_CONST + 1 // reports 1
	}
}
