package jvm.src.main.kotlin

class Caller {
    fun method() {
        Callee().forbiddenMethod()
        Callee()?.toString() // Callee class is excluded but AA still knows that Callee() returns non-nulltable
    }
}
