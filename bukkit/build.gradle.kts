plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "pizzaaxx.bteconosur"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://jitpack.io")
    maven("https://maven.daporkchop.net/")
    maven("https://repo.opencollab.dev/snapshot/")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
}