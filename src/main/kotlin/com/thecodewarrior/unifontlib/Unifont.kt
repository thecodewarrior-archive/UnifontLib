package com.thecodewarrior.unifontlib

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Unifont(val dir: Path) {
    val unifont: GlyphList by lazy {
        val lists = listOf(
                "plane00/unifont-base.hex",
                "plane00/wqy.hex",
                "plane00/hangul-syllables.hex",
                "plane00/spaces.hex",
                "plane00/plane00-unassigned.hex",
                "plane00/plane00-nonprinting.hex",
                "plane00/custom00.hex"
        ).map {
            this.glyphList(it)
        }
        val merged = GlyphList()
        lists.forEach {
            merged.glyphs.putAll(it.glyphs)
        }
        merged
    }

    val unifont_sample: GlyphList by lazy {
        val list = listOf(
                "plane00/copyleft.hex",
                "plane00/hangul-syllables.hex",
                "plane00/plane00-unassigned.hex",
                "plane00/spaces.hex",
                "plane00/wqy.hex",
                "plane00/custom00.hex",
                "plane00/omit.hex",
                "plane00/plane00-nonprinting.hex",
                "plane00/pua.hex",
                "plane00/unifont-base.hex"
        ).map {
            this.glyphList(it)
        }
        val merged = GlyphList()
        list.forEach {
            merged.glyphs.putAll(it.glyphs)
        }
        merged.glyphs.remove(0xFFFE)
        merged.glyphs.remove(0xFFFF)
        merged.glyphs.remove(0x01F12F)

        //TODO: ~ add circles to combining characters in plane00/plane00-combining.txt and not in plane00/plane00-nonprinting.hex
        merged
    }
//
//    val unifont_upper: GlyphList by lazy {
//
//    }
//
//    val upper_combining: GlyphRanges by lazy {
//
//    }
//
//    val upper_nonprinting: GlyphList by lazy {
//
//    }
//
//    val noscript: GlyphList by lazy {
//
//    }
//
//    val upper_combining: GlyphRanges by lazy {
//
//    }
//
//    val unifont_upper_sample: GlyphList by lazy {
//
//    }
//
//    val unifont_csur_lower: GlyphList by lazy {
//
//    }
//
//    val unifont_csur: GlyphList by lazy {
//
//    }
//
//    val csur_combining: GlyphRanges by lazy {
//
//    }
//
//    val unifont_csur_sample: GlyphList by lazy {
//
//    }

    private val glyphListCache = mutableMapOf<String, GlyphList>()
    private fun glyphList(name: String) = glyphListCache.getOrPut(name) {
        val list = GlyphList()
        list.read(this.dir.resolve(name))
        list
    }
}

