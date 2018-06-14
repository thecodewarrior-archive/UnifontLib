package com.thecodewarrior.unifontlib

import java.awt.Color
import java.awt.image.BufferedImage
import java.lang.Math.floor

class Glyph(val codepoint: Int, val width: Int, val height: Int) {
    init {
        if(width % 4 != 0)
            throw IllegalArgumentException("Glyph width not a multiple of 4, it cannot be expressed in hex")
    }

    val image = BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY)

    fun writeToHex(): String {
        val glyphHex = (0 until height).map { y ->
            var row = 0L
            for(x in 0 until width) {
                val mask = 1L shl (width-1-x)
                val pixelSet = (image.getRGB(x, y) and 0xFFFFFF) == 0x000000
                if(pixelSet) {
                    row = row or mask
                }
            }
            row
        }.joinToString("") {
            it.toString(16).padStart(width/4, '0').toUpperCase()
        }

        val codepointHex = codepoint.toString(16).toUpperCase().let {
            if(it.length > 4)
                it.padStart(6, '0')
            else
                it.padStart(4, '0')
        }

        return "$codepointHex:$glyphHex"
    }

    companion object {
        fun readFromHex(line: String): Glyph {
            val lineSplit = line.split(':', limit = 2)
            val codepoint = lineSplit[0].toInt(16)
            val hex = lineSplit[1]
            if(hex.isEmpty() || hex.any { it !in "0123456789abcdefABCDEF" })
                throw IllegalArgumentException("Glyph string `$hex` is not valid hex")

            val height = 16
            val width = floor(hex.length*4.0/16).toInt()

            val glyph = Glyph(codepoint, width, height)
            val rows = hex.chunked(hex.length/height).map { it.toLong(16) }

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val mask = 1L shl (width-1-x)
                    val pixelSet = rows[y] and mask != 0L
                    glyph.image.setRGB(x, y, if(pixelSet) 0x000000 else 0xFFFFFF)
                }
            }

            return glyph
        }

        fun missing(codepoint: Int): Glyph {
            val glyph = Glyph(codepoint, 16, 16)

            val hexDigits = "%04x".format(codepoint).map { it.toString().toInt(16) }

            val g = glyph.image.graphics
            g.color = Color.WHITE
            g.fillRect(0, 0, 16, 16)
            g.color = Color.BLACK
            g.fillRect(1, 1, 14, 14)
            g.drawImage(CommonImages.miniDigits[hexDigits[0]], 2, 1, null)
            g.drawImage(CommonImages.miniDigits[hexDigits[1]], 8, 1, null)
            g.drawImage(CommonImages.miniDigits[hexDigits[2]], 2, 8, null)
            g.drawImage(CommonImages.miniDigits[hexDigits[3]], 8, 8, null)
            g.dispose()

            return glyph
        }

        // potential new format?
        // UUUU:HF...:GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG...
        // U = codepoint
        // H = height/8
        // F = flags (c = combining, n = nonprinting, u = unassigned, p = private use area)
        // G = glyph
    }
}

