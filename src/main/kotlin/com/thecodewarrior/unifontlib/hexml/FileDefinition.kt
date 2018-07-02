package com.thecodewarrior.unifontlib.hexml

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement

sealed class FileDefinition

@XmlRootElement(name = "hex")
class HexFileDefinition: FileDefinition() {
    @XmlAttribute(name = "file", required = true)
    var file = ""

    @XmlElements(
            XmlElement(name = "range", type = CodepointRange::class),
            XmlElement(name = "codepoints", type = Codepoints::class),
            XmlElement(name = "not", type = NotCodpoints::class)
    )
    val files = mutableListOf<CodepointList>()
}

@XmlRootElement(name = "unassigned")
class UnassignedFileDefinition: FileDefinition() {
    @XmlAttribute(name = "file", required = true)
    var file = ""
}

@XmlRootElement(name = "combining")
class CombiningFileDefinition: FileDefinition() {
    @XmlAttribute(name = "file", required = true)
    var file = ""
}
