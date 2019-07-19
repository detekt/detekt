@Suppress("unused", "LongMethod", "LongParameterList", "ComplexCondition", "TooManyFunctions")
class SuppressedElements3 {

    fun lpl(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) = (a + b + c + d + e + f).apply {
        assert(false) { "FAILED TEST" }
    }

    fun cc() {
        if (this is SuppressedElements3 && this !is Any && this is Nothing && this is SuppressedElements3) {
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
