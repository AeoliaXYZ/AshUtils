plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.8"
}

group = "xyz.aeolia"
version = "2.1.11-dev3"
description = "AeoliaLib"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.asheiou.cymru/releases/")
    mavenCentral()
}

dependencies {
    implementation("cymru.asheiou.inv:smart-invs:1.2.8.1")
    implementation("cymru.asheiou:configmanager:1.2.1")
    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("org.apache.commons:commons-text:1.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.essentialsx:EssentialsX:2.21.0") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("net.luckperms:api:5.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.2.0")
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.72.5")
    testImplementation("org.slf4j:slf4j-simple:2.0.7")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories {
        maven {
            name = "asheiou"
            url = if (version.toString().contains("-dev"))
                uri("https://repo.asheiou.cymru/snapshots")
            else
                uri("https://repo.asheiou.cymru/releases")
            credentials {
                username = providers.gradleProperty("asheiouUser").orElse("").get()
                password = providers.gradleProperty("asheiouPassword").orElse("").get()
            }
        }
    }
}

tasks.processResources {
    val projectVer = project.version
    filesMatching("**/*.yml") {
        filter {
            it.replace("%%VERSION", projectVer.toString())
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    dependencies {
        include(dependency("cymru.asheiou:configmanager"))
        include(dependency("org.apache.commons:commons-lang3"))
        include(dependency("org.apache.commons:commons-text"))
    }

    relocate("cymru.asheiou.configmanager", "xyz.aeolia.lib.shade.configmanager")
    relocate("org.apache.commons.lang3", "xyz.aeolia.lib.shade.lang")
    relocate("org.apache.commons.text", "xyz.aeolia.lib.shade.text")
}

tasks.test {
    useJUnitPlatform()
    filter {
        includeTestsMatching("*Test")
    }
}