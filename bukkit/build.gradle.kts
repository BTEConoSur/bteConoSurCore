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
    compileOnly("net.luckperms:api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-core:6.1.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
    implementation("net.dv8tion:JDA:5.0.0-alpha.4")
    implementation("com.github.BuildTheEarth:terraplusplus:terraplusplus:v1.1.0")
    implementation("xyz.upperlevel.spigot.book:spigot-book-api:v1.1.0")
    implementation("fr.minuskube:netherboard-bukki:1.2.2")
}

