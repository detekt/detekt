package cases

/**
 * @author Artur Bosch
 */
class Exceptions {

	fun main() {
		try {
			throw Throwable()
		} catch (e: ArrayIndexOutOfBoundsException) {
			throw Error()
		} catch (e: Error) {
			throw Exception()
		} catch (e: Exception) {
		} catch (e: IllegalMonitorStateException) {
		}  catch (e: IndexOutOfBoundsException) {
			throw RuntimeException()
		} catch (e: Throwable) {
		} catch (e: RuntimeException) {
			throw NullPointerException()
		} catch (e: NullPointerException) {

		}
	}

}
