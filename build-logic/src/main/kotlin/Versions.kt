object Versions {

    const val DETEKT: String = "1.23.0-RC1"
    const val SNAPSHOT_NAME: String = "main"
    const val JVM_TARGET: Int = 8

    fun currentOrSnapshot(): String {
        if (System.getProperty("snapshot")?.toBoolean() == true) {
            return "$SNAPSHOT_NAME-SNAPSHOT"
        }
        return DETEKT
    }
}
