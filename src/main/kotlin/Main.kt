

fun main(args: Array<String>) {
    ALL_TARGETS.map { w ->
        val tries = (::productionSolver).singleRound(w)
        w to tries
    }
    .sortedBy { (w, tries) -> tries.size }
    .forEachIndexed { idx, (w, tries) ->
        println("${(idx + 1).toString().padStart(4, ' ')}) $w ${tries.size}")
    }
}