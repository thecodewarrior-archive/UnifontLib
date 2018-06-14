package com.thecodewarrior.unifontlib

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Unifont {
    val all = GlyphList()
    init {
        all.read(Paths.get("unifont-base.hex"))
    }
    /*
	sort $(UNIFILES) > $(COMPILED_DIR)/unifont-$(VERSION).hex
	(cd $(HEXDIR) && sort *.hex) | \
	  egrep -v "^FFF[EF]" | grep -v "^01F12F:" | \
	   $(BINDIR)/unigencircles $(COMBINING) plane00/plane00-nonprinting.hex \
	   > $(COMPILED_DIR)/unifont_sample-$(VERSION).hex
	sort $(UPPER_FILES) \
	   > $(COMPILED_DIR)/unifont_upper-$(VERSION).hex
	sort $(UPPER_COMBINING) > $(COMPILED_DIR)/upper_combining.txt
	sort plane0[1-E]/plane*-nonprinting.hex \
	   > $(COMPILED_DIR)/upper_nonprinting.hex
	# Generate files in plane01/all directory to create font sample book.
	(cd plane01 && make)
	sort plane01/all/plane01-all.hex plane0[2-E]/ *.hex | egrep -v "^..FFF[EF]" | \
	   $(BINDIR)/unigencircles $(COMPILED_DIR)/upper_combining.txt \
	   $(COMPILED_DIR)/upper_nonprinting.hex \
	   > $(COMPILED_DIR)/unifont_upper_sample-$(VERSION).hex
	# Create a .hex file with CSUR glyphs, without combining circles
	sort $(COMPILED_DIR)/unifont-$(VERSION).hex plane00csur/ *.hex \
	   > $(COMPILED_DIR)/unifont_csur_lower-$(VERSION).hex
	# Create a .hex file with only CSUR glyphs from all planes
	sort plane00csur/ *.hex | sed -e 's/^/00/' \
	   > $(COMPILED_DIR)/unifont_csur-$(VERSION).hex
	sort plane0[1-F]csur/ *.hex \
	   >> $(COMPILED_DIR)/unifont_csur-$(VERSION).hex
	# Create a .hex file with all CSUR glyphs, with combining circles
	sort plane00csur/plane00csur-combining.txt \
	   >  $(COMPILED_DIR)/csur_combining.txt
	sort plane0[1-F]csur/plane0[1-F]csur-combining.txt \
	   >> $(COMPILED_DIR)/csur_combining.txt
	$(BINDIR)/unigencircles \
	     $(COMPILED_DIR)/csur_combining.txt \
	     plane00/plane00-nonprinting.hex \
	   < $(COMPILED_DIR)/unifont_csur-$(VERSION).hex \
	   > $(COMPILED_DIR)/unifont_csur_sample-$(VERSION).hex
    * */
}

class Plane {
    val unassigned = GlyphList() // plane00-unassigned.hex
    val nonprinting = GlyphList() // plane00-nonprinting.hex
    val privateUseArea = GlyphList() // pua.hex
    val unifontBase = GlyphList() // unifont-base.hex
    val wqy = GlyphList() // wqy.hex
    val hangul = GlyphList() // hangul-syllables.hex
    val spaces = GlyphList() // spaces.hex
    val custom00 = GlyphList() // custom00.hex
    val copyleft = GlyphList() // copyleft.hex
    val combining = Unit // plane00-combining.txt
    val upper_combining = Unit // plane0X/*.combining.txt
}

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
        Files.write(path, write().joinToString("\n").toByteArray())
    }

    /**
     * Writes [glyphs] to a list of .hex lines
     */
    fun write(): List<String> {
        return glyphs.values.map { it.writeToHex() }
    }
}

