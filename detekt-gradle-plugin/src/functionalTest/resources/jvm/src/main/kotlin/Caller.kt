package jvm.src.main.kotlin

class Caller {
    fun method() {
        Callee().forbiddenMethod()
    }
}
