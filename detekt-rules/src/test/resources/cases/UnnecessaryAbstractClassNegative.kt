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

// This test case should be removed when type resolution is available - see issue #727
abstract class AbstractClassDerivedFrom : EmptyAbstractClass1() {

    fun f() {}
}

@Ignore
abstract class AbstractClassWithModuleAnnotation {
    abstract fun binds(foo: Integer): Number
}

@jdk.nashorn.internal.ir.annotations.Ignore
abstract class AbstractClassWithModuleAnnotation {
    abstract fun binds(foo: Integer): Number
}
