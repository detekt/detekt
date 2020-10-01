object Versions {

    const val DETEKT: String = "1.14.1"
    const val JVM_TARGET: String = "1.8"
    const val JACOCO: String = "0.8.6"

    fun currentOrSnapshot(): String {
        if (System.getProperty("snapshot")?.toBoolean() == true) {
            return "$DETEKT-SNAPSHOT"
        }
        return DETEKT
    }
}
