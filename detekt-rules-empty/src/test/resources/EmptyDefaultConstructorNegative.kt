@file:Suppress("unused", "UNUSED_PARAMETER")

package cases

class PrimaryConstructorWithParameter constructor(x: Int)

class PrimaryConstructorWithAnnotation @SafeVarargs constructor()

class PrivatePrimaryConstructor private constructor()

class EmptyConstructorIsCalled() {

    constructor(i: Int) : this()
}
