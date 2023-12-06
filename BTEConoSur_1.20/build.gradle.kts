import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "pizzaaxx.bteconosur"
version = "3.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://maven.daporkchop.net/")
    maven("https://repo.opencollab.dev/snapshot/")
    maven("https://maven.elmakers.com/repository/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.2.17-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.17-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9-SNAPSHOT")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("net.dv8tion:JDA:5.0.0-beta.5")
    implementation("com.github.SmylerMC:terraminusminus:5907790da3")
    implementation("xyz.upperlevel.spigot.book:spigot-book-api:1.6")
    implementation("com.mysql:mysql-connector-j:8.0.32")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.github.micycle1:Clipper2-java:1.0.6")
    implementation("com.github.FreshLlamanade:polylabel-java:7639b75b53")
    compileOnly("me.clip:placeholderapi:2.11.2")
    implementation("org.jogamp.gluegen:gluegen-rt-main:2.3.2")
    implementation("org.jogamp.jogl:jogl-all-main:2.3.2")
    implementation("com.networknt:json-schema-validator:1.0.72")
    implementation("com.github.PeterMassmann:SQL-Manager:83bef5aecb")
    implementation("fr.mrmicky:fastboard:2.0.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<ShadowJar> {
        relocate("fr.mrmicky.fastboard", "pizzaaxx.bteconosur.fastboard")
    }
}
