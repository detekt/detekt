package cases.nullable

class UnsafeCallOnNullable {
    fun test(str: String?) {
        println(str!!.length)
    }
}