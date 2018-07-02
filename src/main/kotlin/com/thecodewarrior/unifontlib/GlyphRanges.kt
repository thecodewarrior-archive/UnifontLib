package com.thecodewarrior.unifontlib

import com.thecodewarrior.unifontlib.utils.codepointHex
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.math.max
import kotlin.math.min

class CodepointSet {
    private val head: Entry? = null

    fun add(codepoint: Int) {
    }

    private operator fun Collection<IntRange>.contains(value: Int) = this.any { value in it }
}

private data class Entry(var min: Int, var max: Int, var previous: Entry?, var next: Entry?) {

    fun insert(entry: Entry, asNext: Boolean = true) {
        if(asNext) {
            entry.previous = this
            entry.next = next

            next?.previous = entry
            this.next = entry
        } else {
            entry.next = this
            entry.previous = previous

            previous?.next = entry
            this.previous = entry
        }
    }

    fun remove() {
        next?.previous = previous
        previous?.next = next

        // clear out the invalid data. Someone else might hold a reference to this.
        this.previous = null
        this.next = null
    }

    /**
     * Tries to insert the given range. Returns true if the insertion has been handles, false if the next entry should
     * be checked.
     */
    fun handleInsert(value: IntRange): Boolean {
        if(value.first >= min && value.last <= max) { // it is inside us
            return true
        }

        if(value.last < min-1) { // the value is behind us, disconnected
            this.insert(Entry(value.first, value.last, previous, next), asNext = false)
            return true
        }

        if(value.first > max+1) { // the value is after us, disconnected
            return false // signal the loop to offload it to the next one
        }

        // if we get here the range is neither inside us nor disconnected from us, thus it must be touching us

        // expand to engulf the inserted range
        this.min = min(this.min, value.first)
        this.max = max(this.max, value.last)

        // Progressively swallow the next entries that overlap or touch us:
        var next = this.next
        while(next != null && next.min <= this.max+1) { // while `next` overlaps or touches us
            this.max = max(this.max, next.max)
            next.remove()
            next = this.next
        }
        // (We don't care about those before us. If it touched them they would have dealt with it)

        return false
    }
}

/*
    fun readFromHex(path: Path) {
        ranges.clear()

        val lines = path.toFile().readLines()
        val codepoints = lines.map { it.split(':')[0].toInt(16) }.sorted()

        var start = Int.MIN_VALUE
        var end = Int.MIN_VALUE
        codepoints.forEach {
            if (start < 0) {
                start = it
                end = it
            } else if (end == it - 1) {
                end = it
            } else {
                ranges.add(start..end)
                start = Int.MIN_VALUE
                end = Int.MIN_VALUE
            }
        }
    }

//    fun simplify(mergeRatio: Double = 0.25) {
//        var i = 0
//        while(i < ranges.size-1) {
//            val current = ranges[i]
//            val next = ranges[i+1]
//            val gap = (current.endInclusive + 1) .. (next.start - 1)
//            if(gap.count() < current.count() * mergeRatio || gap.count() < next.count() * mergeRatio) {
//                notRanges.add(gap)
//                ranges.removeAt(i)
//                ranges.removeAt(i)
//                ranges.add(i, current.start .. next.endInclusive)
//            } else {
//                i++
//            }
//        }
//    }

    fun writeToRanges(path: Path) {
        val lines = ranges.map {
            if(it.first == it.last)
                it.first.codepointHex()
            else
                "${it.first.codepointHex()} ${it.last.codepointHex()}"
        }
        path.toFile().writeText(lines.joinToString("\n"))
    }

    fun readFromRanges(path: Path) {
        ranges.clear()
        val lines = path.toFile().readLines()
        lines.filter { !(it.isEmpty() || it.startsWith(';')) }.forEach { line ->
            val split = line.trim().split(' ').map { it.trim().toInt(16) }
            if(split.size == 1) {
                ranges.add(split[0]..split[0])
            } else {
                ranges.add(split[0]..split[1])
            }
        }
    }
}
*/
