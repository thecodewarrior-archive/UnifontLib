package com.thecodewarrior.unifontlib.hexml

import com.thecodewarrior.unifontlib.CodepointSet
import java.nio.file.Path
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElements
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

sealed class CodepointList {
    abstract fun doesInclude(codepoint: Int): Boolean
    abstract fun doesExclude(codepoint: Int): Boolean

    abstract val included: Iterable<Int>
    abstract val excluded: Iterable<Int>
}

@XmlRootElement(name = "range")
class CodepointRange: CodepointList() {

    @XmlAttribute( name = "from", required = true)
    var start: Int = 0

    @XmlAttribute( name = "to", required = true)
    var end: Int = 0

    override fun doesInclude(codepoint: Int): Boolean {
        return (start..end).contains(codepoint)
    }

    override fun doesExclude(codepoint: Int): Boolean {
        return false
    }

    override val included: Iterable<Int>
        get() = start..end
    override val excluded: Iterable<Int>
        get() = listOf()
}

@XmlRootElement(name = "codepoints")
class Codepoints: CodepointList() {

    @XmlAttribute(name = "file")
    var file: String = ""


    private var set = CodepointSet()

    fun read(basePath: Path) {
        val lines = basePath.resolve(file).toFile().readLines()
                .map { it.split(':')[0] } // remove the character from hex files, if this is a hex file
    }

    override fun doesInclude(codepoint: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doesExclude(codepoint: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val included: Iterable<Int>
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    override val excluded: Iterable<Int>
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

@XmlRootElement(name = "not")
class NotCodpoints: CodepointList() {
    @XmlElements(
            XmlElement(name = "range", type = CodepointRange::class),
            XmlElement(name = "codepoints", type = Codepoints::class),
            XmlElement(name = "not", type = NotCodpoints::class)
    )
    val sublists = mutableListOf<CodepointList>()

    override fun doesInclude(codepoint: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doesExclude(codepoint: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val included: Iterable<Int>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val excluded: Iterable<Int>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}
