@file:Suppress("unused", "RemoveEmptyPrimaryConstructor")

package cases

import jdk.nashorn.internal.ir.annotations.Ignore

abstract class AbstractClassOk {

    abstract val i: Int
    fun f() {}
}

abstract class AbstractClassWithPrimaryConstructorConcretePropertyOk(val i: Int) {
    abstract fun f()
}

// empty abstract classes should not be reported by this rule
abstract class EmptyAbstractClass1

abstract class EmptyAbstractClass2()

abstract class AbstractClassDerivedFrom1 : AbstractClassOk() {
    fun g() {}
}

abstract class AbstractClassDerivedFrom2 : AbstractClassWithPrimaryConstructorConcretePropertyOk(0) {
    fun g() {}
}

abstract class AbstractClassDerivedFrom3 : Interface {
    fun g() {}
}

interface Interface {
    fun f()
}

@Ignore
abstract class AbstractClassWithModuleAnnotation {
    abstract fun binds(foo: Integer): Number
}

@jdk.nashorn.internal.ir.annotations.Ignore
abstract class AbstractClassWithModuleAnnotation {
    abstract fun binds(foo: Integer): Number
}
