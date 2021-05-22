package cases

class OutOfOrder(private val x: String) {

    val y = x

    init {
        check(x == "yes")
    }

    constructor(z: Int): this(z.toString())

    fun returnX() = x

    companion object {
        const val IMPORTANT_VALUE = 3
    }
}
