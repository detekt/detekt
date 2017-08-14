package cases

@Suppress("unused")
class EmptyPrimaryConstructor constructor() {

}

@Suppress("unused")
class EmptyPublicPrimaryConstructor public constructor() {

}

@Suppress("unused")
class PrimaryConstructorWithParameter constructor(x: Int) {

}

@Suppress("unused")
class PrimaryConstructorWithAnnotation @SafeVarargs constructor() {

}

@Suppress("unused")
class PrivatePrimaryConstructor private constructor() {

}
