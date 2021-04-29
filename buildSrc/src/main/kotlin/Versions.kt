object Versions {

    const val DETEKT: String = "1.16.0"
    const val SNAPSHOT_NAME: String = "main"
    const val JVM_TARGET: String = "1.8"
    const val JACOCO: String = "0.8.6"

    fun currentOrSnapshot(): String {
        if (System.getProperty("snapshot")?.toBoolean() == true) {
            return "$SNAPSHOT_NAME-SNAPSHOT"
        }
        return DETEKT
    }
}
