import java.nio.file.Files
import java.nio.file.Path
import kotlin.random.Random

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
}

@JvmInline
value class YellowMask(val mask: Int) {
    fun asCharSet(): String = ('a'..'z').fold("") { l, r ->
        if (mask and (1 shl (r - 'a')) != 0) (l + r) else l
    }

    fun inverted() = YellowMask(mask.inv())

    fun or(other: YellowMask) = YellowMask(mask or other.mask)
    fun and(other: YellowMask) = YellowMask(mask and other.mask)

    companion object {
        fun fromWord(s: String) = YellowMask(s.toCharArray().fold(0) { l, r ->
            l or (1 shl (r - 'a'))
        })
    }
}

fun GuessWord.print() = joinToString("")

fun List<GuessWord>.println() = withIndex().joinToString("\n") {
    (idx, it)  -> "${(idx + 1).toString().padStart(4, ' ')}) ${it.print()}"
}

typealias Solver = (prev: List<GuessWord>) -> String

fun List<WordChar>.solved() = all { it.type == Type.GREEN }

val ALL_WORDS = Files.readString(Path.of("words.txt")).lines().toSet()
val ALL_TARGETS = Files.readString(Path.of("targets.txt")).lines().toSet()

val FREQS = Array(5) {
    Array(26) { 0 }
}.also {
    for (w in ALL_TARGETS) {
        it.forEachIndexed { index, ints ->
            ints[w[index] - 'a']++
        }
    }
}

fun String.quality() = let { w ->
    FREQS
        .withIndex()
        .map { (index, ints) ->
            w[index] to ints[w[index] - 'a']
        }
        .distinctBy {
            it.first
        }
        .sumOf {
            it.second
        }
}

fun Solver.singleRound(word: String): List<List<WordChar>> {
    val wordChars = word.toCharArray()
    val prevGueses = mutableListOf<List<WordChar>>()

    while (true) {
        val guess = this.invoke(prevGueses)

        val res = guess.mapIndexed { i, c ->
            WordChar(
                guess[i],
                if (!wordChars.contains(c)) {
                    Type.BLACK
                } else {
                    if (wordChars[i] == c) {
                        Type.GREEN
                    } else {
                        Type.YELLOW
                    }
                }
            )
        }
        prevGueses.add(res)

        if (res.solved()) {
            return prevGueses
        }
    }
}

val warmup = listOf("erase", "booty", "chill")
    // listOf("slate", "crony", "humid")
    // listOf("later", "sonic", "pudgy",)

val rnd = Random(42)

fun productionSolver(guesses: List<GuessWord>): String {
    if (guesses.size < warmup.size) {
        return warmup[guesses.size]
    } else {
        val greens = guesses.flatMap {
            it.withIndex().filter { (_, it) ->
                it.type == Type.GREEN
            }.map { (idx, it) ->
                idx to it.sym
            }
        }.distinct()

        val yellows = guesses.flatMap {
            it.filter { it.type == Type.YELLOW }.map {  it.sym }
        }.distinct()

        val blacks = guesses.flatMap {
            it.filter { it.type == Type.BLACK }.map {  it.sym }
        }.distinct()

        val prevWords = guesses.map {
            String(it.map {  it.sym }.toCharArray())
        }.toSet()

        val allVariants = ALL_TARGETS.filter { w ->
            (w !in prevWords) && greens.all { (idx, char) ->
                w[idx] == char
            } && yellows.all {
                it in w
            } && blacks.all {
                it !in w
            }
        }

        // if (allVariants.size == 1) {
            return allVariants.random(rnd)
        /*
        } else {
            // We need to select a word to split space in a maximum way
        }

         */
    }
}

// fun splitOnGroups(input: List<String>, )