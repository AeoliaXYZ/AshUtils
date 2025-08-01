plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("com.gradleup.shadow") version "8.3.8"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/groups/public/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.essentialsx.net/releases/")
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "asheiou"
        url = uri("https://repo.asheiou.cymru/releases/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("cymru.asheiou.inv:smart-invs:1.2.8.1")
    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("org.apache.commons:commons-text:1.1")
    implementation(libs.cymru.asheiou.configmanager)
    implementation(libs.org.jetbrains.kotlin.kotlin.stdlib.jdk8)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core.jvm)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json.jvm)
    implementation(libs.org.jetbrains.kotlin.kotlin.serialization.compiler.plugin)
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.net.essentialsx.essentialsx) {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }
    compileOnly(libs.com.github.milkbowl.vaultapi) {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly(libs.net.luckperms.api)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.slf4j:slf4j-simple:2.0.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.2.0")
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.72.5")
}

group = "xyz.aeolia"
version = "2.1.4-dev1"
description = "AeoliaLib"
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories {
        maven {
            name = "asheiou"
            url = if (project.version.toString().contains("-dev")) uri("https://repo.asheiou.cymru/snapshots")
            else uri("https://repo.asheiou.cymru/releases")
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
        include(dependency("org.jetbrains.kotlin:"))
        include(dependency("org.jetbrains.kotlinx:"))
        include(dependency("org.apache.commons:commons-lang3"))
        include(dependency("org.apache.commons:commons-text"))
        exclude(dependency("org.jetbrains.kotlin:kotlin-serialization-compiler-plugin"))
    }

    relocate("kotlin", "xyz.aeolia.lib.shade.kotlin")
    relocate("kotlinx", "xyz.aeolia.lib.shade.kotlinx")
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