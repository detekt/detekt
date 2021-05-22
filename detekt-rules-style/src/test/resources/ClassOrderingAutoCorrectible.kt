package cases

class OutOfOrder(private val x: String) {

    companion object {
        const val IMPORTANT_VALUE = 3
    }

    fun returnX() = x

    constructor(z: Int): this(z.toString())

    val y = x

    init {
        check(x == "yes")
    }
}
