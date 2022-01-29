
fun main() {
    val bestLetters = MutableList<MutableMap<Char, Int>>(5) { HashMap() }

    ALL_TARGETS.forEach {
        it.toCharArray().forEachIndexed { idx, ch ->
            bestLetters[idx].let { map ->
                map[ch] = (map[ch] ?: 0) + 1
            }
        }
    }
/*
    println(
        bestLetters.toList().sortedBy { it.second }.joinToString("\n") { (c, cnt) ->
            "$c -> $cnt"
        }
    )
*/
    fun String.charCount() = toCharArray()
        .withIndex()
        .sumOf { (idx, c) -> ((bestLetters[idx][c] ?: 0 ) * 2 + bestLetters.sumOf { it[c] ?: 0 }) }

    val best = mutableListOf<String>()
    var all = ALL_TARGETS.toList()
    (0..2).forEach {
        val sortedBy = all.sortedBy {
            it.charCount()
        }

        val bestWord = sortedBy.last().toCharArray()

        best.add(sortedBy.last())

        all = all.filter { w ->
            w.all { it !in bestWord }
        }
    }

    println(best.joinToString(", ") { "\"$it\"" })
}