package cases

import java.io.IOException

fun swallowedExceptions() {
    try {
    } catch (e: Exception) {
        throw IOException(e.message) // violation
    } catch (e: Exception) {
        throw Exception(IOException(e.toString())) // violation
    } catch (e: Exception) {
        throw IOException(e.message) // violation
    } catch (e: Exception) {
        throw IOException() // violation
    }
}

fun unusedException() {
    try {
    } catch (e: IOException) {
        println() // violation
    }
}
