package cases

@Suppress("unused", "UNUSED_VARIABLE")
fun preferToOverPairSyntaxPositive() {
	val pair1 = Pair(1, 2)

	val pair2: Pair<Int, Int> = Pair(1, 2)

	val pair3 = Pair(Pair(1, 2), Pair(3, 4))
}
