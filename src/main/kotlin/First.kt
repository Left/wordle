
fun main() {
    /*
    splitOnGroups(ALL_TARGETS,"roate", Masks(
        GreenMask.empty(),
        YellowMask.NONE,
        YellowMask.NONE
    )).forEach { t, u ->
       println("$t:\n${u.joinToString("\n") { "\t$it" }}")
    }

     */

    ALL_WORDS.map { w ->
        w to splitOnGroups(ALL_TARGETS, w, Masks(
            GreenMask.empty(),
            YellowMask.NONE,
            YellowMask.NONE
        )).size
    }
    .sortedBy { it.second }
    .forEachIndexed { idx, it ->
        println("${idx.toString().padStart(4, ' ')}) ${it.first} ${it.second}")
    }


}