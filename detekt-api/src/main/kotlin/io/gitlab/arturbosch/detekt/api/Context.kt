package io.gitlab.arturbosch.detekt.api

class Context {

    /**
     * Returns a list of violations.
     */
    val findings: List<Finding>
        get() = _findings

    private val _findings: MutableList<Finding> = mutableListOf()

    fun report(finding: Finding) {
        val ktElement = finding.entity.ktElement
        if (ktElement == null || !ktElement.isSuppressedBy(finding.issue.id)) {
            _findings += finding
        }
    }

//    fun <K, V> List<Pair<K, List<V>>>.toMergedMap(): Map<K, List<V>> {
//        val map = HashMap<K, MutableList<V>>()
//        this.forEach {
//            map.merge(it.first, it.second.toMutableList(), { l1, l2 -> l1.apply { addAll(l2) } })
//        }
//        return map
//    }
}