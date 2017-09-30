@file:Suppress("ConstantConditionIf")

package cases

import java.io.IOException

fun x() {
	try {
	} catch(e: Exception) {
		throw IOException(e.message) // violation
	} catch(e: Exception) {
		throw Exception(IOException(e.toString())) // violation
	} catch(e: Exception) {
		if (true) {
			throw IOException(e.message) // violation
		}
		throw Exception(e)
	} catch (e: Exception) {
		println()
	} catch (e: Exception) {
		throw IOException()
	} catch(e: Exception) {
		throw IOException(e.message, e)
	} catch(e: Exception) {
		throw IOException(e)
	}
}
