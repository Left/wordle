import java.nio.file.Files
import java.nio.file.Path
import kotlin.random.Random

/*
enum class Type(val bg: String) {
    BLACK("100"),
    YELLOW("103"),
    GREEN("102"),
}

class WordChar(
    val sym: Char,
    val type: Type,
) {
    override fun toString() = "\u001B[30m\u001b[${type.bg}m${sym}\u001B[0m"
}

typealias GuessWord = List<WordChar>

fun GuessWord.toWord() = fold("") { l, r -> l + r }
 */

@JvmInline
value class GreenMask(val mask: Int) {
    companion object {
        fun empty() = GreenMask(0)
        fun betweenWords(target: String, candidate: String) = GreenMask(target.foldIndexed(0) { idx, l, r ->
            l or (if (candidate[idx] == r) (r - 'a' + 1) else 0).shl(idx*5)
        })
    }

    override fun toString() = (0..4).fold("") { l, idx ->
        l + (mask shr (5*idx)).and(0x1f).let { if (it == 0) '_' else ('a' + it - 1) }
    }

    fun binStr() = mask.toString(2)
        .padStart(25, '0')
        .chunked(5)
        .reversed()
        .joinToString("") {
            it.toInt(2).let {
                if (it == 0) {
                    "_"
                } else {
                    ('a' + it - 1).toString()
                }
            }
        }

    fun sutisfies(w: String): Boolean =
        (0..4).fold(true) { l, idx ->
            l && (mask shr (idx*5)).and(0x1f).let {
                (it == 0) || (w[idx] == ('a' + it - 1))
            }
        }

    fun isSolved() = (0..4).fold(true) { l, idx ->
        l && (mask shr (idx*5)).and(0x1f).let {
            (it != 0)
        }
    }

    infix fun or(betweenWords: GreenMask) = GreenMask(mask or betweenWords.mask)
}

@JvmInline
value class YellowMask(val mask: Int) {
    fun asCharSet(): String = ('a'..'z').fold("") { l, r ->
        if (mask and (1 shl (r - 'a')) != 0) (l + r) else l
    }

    override fun toString() = asCharSet()

    fun inverted() = YellowMask(mask.inv())

    infix fun or(other: YellowMask) = YellowMask(mask or other.mask)
    infix fun and(other: YellowMask) = YellowMask(mask and other.mask)

    companion object {
        fun fromWord(s: String) = YellowMask(s.toCharArray().fold(0) { l, r ->
            l or (1 shl (r - 'a'))
        })

        fun fromWordInversed(s: String) = YellowMask(s.toCharArray().fold(0) { l, r ->
            l or (1 shl (r - 'a')).inv().and(1)
        })

        val NONE = YellowMask(0)
        val ALL = YellowMask(0x7fff_ffff)
    }
}

// fun GuessWord.print() = joinToString("")
/*
fun List<GuessWord>.println() = withIndex().joinToString("\n") {
    (idx, it)  -> "${(idx + 1).toString().padStart(4, ' ')}) ${it.print()}"
}
*/

typealias Solver = (history: List<String>, prev: Masks) -> String

val ALL_WORDS = Files.readString(Path.of("words.txt")).lines().toSet()
val ALL_TARGETS = Files.readString(Path.of("targets.txt")).lines().toSet()

fun Solver.singleRound(word: String): List<String> {
    val prevGueses = mutableListOf<String>()
    var mask = Masks.EMPTY

    while (true) {
        val guess = this.invoke(prevGueses, mask)
        mask = mask.applyWord(guess, word)

        prevGueses.add(guess)

        if (mask.isSolved()) {
            return prevGueses
        }
    }
}

val rnd = Random(42)

fun productionSolver(history: List<String>, mask: Masks): String {
    if (mask.yellowMask.mask == 0 && mask.blackMask.mask == 0) {
        //
        return "trace"
    } else {
        val filteredTargets = ALL_TARGETS.filter {
            mask.sutisfies(it)
        }.filter { it !in history }

        if (filteredTargets.size <= 3) {
            return filteredTargets.first()
        } else {
            var goodFound = false
            return ALL_WORDS
                .filter { it !in history }
                .maxByOrNull { w ->
                    if (goodFound) {
                        Int.MIN_VALUE
                    } else {
                        splitOnGroups(filteredTargets, w, mask).let {
                            // println("$mask $w (${it.size}) \t$it")
                            if (it.containsKey(mask)) {
                                Int.MIN_VALUE
                            } else {
                                goodFound = it.size == filteredTargets.size
                                it.size
                            }
                        }
                    }
                }!!
        }
    }
}

/**
 * This is the state of field
 */
data class Masks(
    val greenMask: GreenMask,
    val yellowMask: YellowMask,
    val blackMask: YellowMask,
) {
    override fun toString() = "{ $greenMask '$yellowMask' '$blackMask' }"

    companion object {
        val EMPTY = Masks(GreenMask.empty(), YellowMask.NONE, YellowMask.NONE)
    }

    fun applyWord(word: String, target: String) =
        Masks(
            greenMask or GreenMask.betweenWords(target, word),
            yellowMask or (YellowMask.fromWord(word) and YellowMask.fromWord(target)),
            blackMask or (YellowMask.fromWord(word) and YellowMask.fromWord(target).inverted())
        )

    fun sutisfies(it: String) =
        greenMask.sutisfies(it) &&
                yellowMask.and(YellowMask.fromWord(it)) == yellowMask &&
                blackMask.and(YellowMask.fromWord(it).inverted()) == blackMask

    fun isSolved() = greenMask.isSolved()
}

fun splitOnGroups(input: Iterable<String>, word: String, masks: Masks): Map<Masks, List<String>> {
    return input.groupBy {
        masks.applyWord(word, it)
    }
}