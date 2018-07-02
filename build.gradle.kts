import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileOutputStream

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.31"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin(module="gradle-plugin", version=kotlin_version))
    }
}

plugins {
    java
    application
}

group = "com.thecodewarrior.unifontlib"
version = "1.0-SNAPSHOT"

apply {
    plugin("kotlin")
}

val kotlin_version: String by extra

application {
    mainClassName = "com.thecodewarrior.unifontlib.commands.MainKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin(module="stdlib-jdk8", version=kotlin_version))
    compile("com.github.ajalt", "clikt", "1.2.0")
    compile("commons-net", "commons-net", "3.6")
    compile("org.rauschig", "jarchivelib", "0.7.1")
    testCompile("junit", "junit", "4.12")
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName="${project.name}-fat"
    manifest {
        attributes["Main-Class"] = "com.thecodewarrior.unifontlib.commands.MainKt"
    }
    from(configurations.runtime.map({ if(it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}

val createExecutable = task("createExecutable") {
    dependsOn(fatJar)
    val input = fatJar.outputs.files.singleFile
    val output = project.buildDir.resolve("libs/${project.name}-${project.version}")
    doLast {
        FileOutputStream(output).use { outputStream ->
            exec {
                standardOutput = outputStream
                commandLine("cat", "src/launcher-stub.sh", input.absolutePath)
            }
        }
        exec {
            commandLine("chmod", "+x", output.absolutePath)
        }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks["build"].dependsOn(createExecutable)