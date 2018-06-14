import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.31"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
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
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}