import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val l = Files.readString(Path.of("words.txt"))
        .lines()
        .filter { it.isNotEmpty() }

    fun String.charCount() = toCharArray().toSet().size

    println(l.sortedBy {
        it.charCount()
    }.joinToString("\n") { it + " " + it.charCount() } )
}