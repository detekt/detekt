package jvm.src.main.kotlin

class Caller {
    fun method() {
        Callee().forbiddenMethod()
        Callee()?.toString() // Callee class is excluded but AA still knows that Callee() returns non-nullable
        Book.serializer()?.toString() // serializer() is created by compiler plugin but AA still knows that serializer() returns non-nullable
    }
}
