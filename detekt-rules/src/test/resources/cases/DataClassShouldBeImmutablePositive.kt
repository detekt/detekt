@file:Suppress("unused")

package cases

data class MutableDataClass1(
    val i: Int,
    var s: String
)

data class MutableDataClass2(
    val i: Int
) {

    var s: String? = null
}

data class MutableDataClass3(
    val i: Int
) {

    var s: String = ""
        private set
}

data class MutableDataClass4(
    val i: Int
) {

    lateinit var s: String
}
