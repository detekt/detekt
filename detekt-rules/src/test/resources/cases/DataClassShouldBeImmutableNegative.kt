@file:Suppress("unused")

package cases

data class ImmutableDataClass1(
    val i: Int,
    val s: String
)

data class ImmutableDataClass2(
    val i: Int
) {

    val s: String? = null
}

data class ImmutableDataClass3(
    val i: Int
) {

    val s: String by lazy { "" }
}

class MutableClass1(
    val i: Int,
    var s: String
)

class MutableClass2(
    val i: Int
) {

    var s: String = ""
}
