package cases

/**
 * @author Marvin Ramin
 */
const val test = 1

fun test() {
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(test)
	println(1)
	println(1)
	for (i in 0..500) {
		println(i)
		longMethod()
	}
}

fun longMethod() {
	if (true) {
		if (true) {
			if (true) {
				5.run {
					this.let {
						listOf(1, 2, 3).map { it * 2 }
								.groupBy(Int::toString, Int::toString)
					}
				}
			}
		}
	}

	try {
		for (i in 1..5) {
			when (i) {
				1 -> print(1)
			}
		}
	} finally {

	}
}

@Suppress("unused")
/**
 * Top level members must be skipped for LargeClass rule
 */
val aTopLevelProperty = 0
