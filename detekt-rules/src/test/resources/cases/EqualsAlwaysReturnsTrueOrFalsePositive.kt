@file:Suppress("EqualsOrHashCode", "unused", "UNREACHABLE_CODE")

package cases

// reports 1 for every equals method
class EqualsReturnsTrue {

    override fun equals(other: Any?): Boolean {
        return true
    }
}

class EqualsReturnsFalse {

    override fun equals(other: Any?): Boolean {
        return false
    }
}

class EqualsReturnsFalseWithUnreachableReturnStatement {

    override fun equals(other: Any?): Boolean {
        return false
        return true
    }
}

class EqualsReturnsFalseWithUnreachableCode {

    override fun equals(other: Any?): Boolean {
        return false
        val i = 0
    }
}

class EqualsReturnsConstantExpression {

    override fun equals(other: Any?) = false
}

class EqualsWithTwoReturnExpressions {

    override fun equals(other: Any?): Boolean {
        if (other is Int) {
            return true
        }
        return true
    }
}
