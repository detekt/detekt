@file:Suppress("unused", "LongMethod", "LongParameterList", "ComplexCondition")

fun lpl2(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
    assert(false) { "FAILED TEST" }
}

class SuppressedElements2 {

    fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
        assert(false) { "FAILED TEST" }
    }

    fun cc() {
        if (this is SuppressedElements2 && this !is Any && this is Nothing && this is SuppressedElements2) {
            assert(false) { "FAIL" }
        }
    }

    fun lm() {
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        lpl(1, 2, 3, 4, 5, 6)
        assert(false) { "FAILED TEST" }
    }

}
