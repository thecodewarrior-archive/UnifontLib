package com.thecodewarrior.unifontlib.hexml

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElements
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "hexml")
class Hexml {

    @XmlElements(
            XmlElement(name = "hex", type = HexFileDefinition::class)
    )
    lateinit var files: List<FileDefinition>
}