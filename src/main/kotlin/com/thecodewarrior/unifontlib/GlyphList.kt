package com.thecodewarrior.unifontlib

import java.nio.file.Files
import java.nio.file.Path

class GlyphList {
    val glyphs = mutableMapOf<Int, Glyph>()

    /**
     * Reads the .hex file at [path] and replaces [glyphs] with its decoded content
     */
    fun read(path: Path) {
        read(Files.readAllLines(path))
    }

    /**
     * Reads the passed .hex [lines] and replaces [glyphs] with their decoded content
     */
    fun read(lines: List<String>) {
        glyphs.clear()
        lines.forEach { line ->
            val glyph = Glyph.readFromHex(line.trim())
            glyphs[glyph.codepoint] = glyph
        }
    }

    /**
     * Writes [glyphs] to [path], overwriting its existing content
     */
    fun write(path: Path) {
        Files.write(path, write())
    }

    /**
     * Writes [glyphs] to a list of .hex lines
     */
    fun write(): List<String> {
        val sixDigit = glyphs.keys.any { it > 0xFFFF }
        if(sixDigit) {
            return glyphs.values.map { it.writeToHex().replace("^[0-9A-Fa-f]{4}:".toRegex()) { "00" + it.value } }
        } else {
            return glyphs.values.map { it.writeToHex() }
        }
    }
}

