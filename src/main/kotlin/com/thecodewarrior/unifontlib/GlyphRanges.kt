package com.thecodewarrior.unifontlib

import java.nio.file.Files
import java.nio.file.Path

class GlyphRanges {
    val headers = mutableMapOf<Int, String>()
    val ranges = mutableListOf<IntRange>()

    fun read(path: Path) {
        read(Files.readAllLines(path))
    }

    fun read(lines: List<String>) {
        ranges.clear()
        headers.clear()
        lines.forEach { line ->
            if(line.startsWith(";")) {
                headers[lines.size] = line
            } else {
                val pair = line.split(' ').map { it.toInt(16) }
                ranges.add(IntRange(pair[0], pair.getOrElse(1) { pair[0] }))
            }
        }
    }

    fun write(path: Path) {
        Files.write(path, write())
    }

    fun write(): List<String> {
        val lines = mutableListOf<String>()
        ranges.forEachIndexed { index, range ->
            headers[index]?.also {
                lines.add(it)
            }
            if(range.first == range.last) {
                lines.add(range.first.toString(16))
            } else {
                lines.add(range.first.toString(16) + " " + range.last.toString(16))
            }
        }
        headers[lines.size]?.also {
            lines.add(it)
        }
        return lines
    }
}