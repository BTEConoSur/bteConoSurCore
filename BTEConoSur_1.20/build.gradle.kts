import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "pizzaaxx.bteconosur"
version = "3.0"

repositories {
    maven("https://maven.geotoolkit.org/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://maven.daporkchop.net/")
    maven("https://repo.opencollab.dev/snapshot/")
    maven("https://maven.elmakers.com/repository/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.osgeo.org/repository/release/")
    mavenCentral()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.2.18-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.18-SNAPSHOT")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("net.dv8tion:JDA:5.0.0-beta.5")
    implementation("com.github.SmylerMC:terraminusminus:5907790da3")
    implementation("com.mysql:mysql-connector-j:8.0.32")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.github.micycle1:Clipper2-java:1.0.6")
    implementation("com.github.FreshLlamanade:polylabel-java:7639b75b53")
    compileOnly("me.clip:placeholderapi:2.11.2")
    implementation("com.networknt:json-schema-validator:1.0.72")
    implementation("com.github.PeterMassmann:SQL-Manager:226907b0b2")
    implementation("fr.mrmicky:fastboard:2.0.2")
    implementation("org.geotools:gt-shapefile:27.0")
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
