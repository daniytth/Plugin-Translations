plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

group = "net.flamestudios"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.canvasmc.io/snapshots")
    maven("https://maven.enginehub.org/repo/")

    maven("https://jitpack.io")
    maven("https://repo.tcoded.com/releases")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    compileOnly(libs.canvas)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.folialib)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.worldedit)
    compileOnly(libs.worldguard)

    compileOnly(libs.vault) {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${project.version}.jar"
        archiveClassifier = null

        relocate("com.tcoded.folialib", "de.flamesmp.libs.folialib")

        mergeServiceFiles()
        exclude("META-INF/maven/**")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

        manifest {
            attributes["Implementation-Version"] = rootProject.version
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }

    withType<Javadoc>() {
        options.encoding = Charsets.UTF_8.name()
    }

    defaultTasks("build")

    val version = "1.21.11"
    val javaVersion = JavaLanguageVersion.of(21)

    val jvmArgsExternal = listOf(
        "-Dcom.mojang.eula.agree=true"
    )

    runServer {
        minecraftVersion(version)
        runDirectory = rootDir.resolve("run/paper/$version")

        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = javaVersion
        }

        downloadPlugins {
            url("https://github.com/ViaVersion/ViaVersion/releases/download/5.7.1/ViaVersion-5.7.1.jar")
            url("https://github.com/ViaVersion/ViaBackwards/releases/download/5.7.1/ViaBackwards-5.7.1.jar")
        }

        jvmArgs = jvmArgsExternal
    }
}