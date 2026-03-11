import org.gradle.kotlin.dsl.implementation
import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    val spongeGradleVersion = "2.2.0"

    `java-library`
    id("org.spongepowered.gradle.plugin") version spongeGradleVersion
    id("org.spongepowered.gradle.ore") version spongeGradleVersion // for Ore publishing
    id("com.gradleup.shadow") version "9.3.0"
}

group = "me.brynview.navidrohim"
version = "1.2.3-1.21.11-alpha.2"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.slf4j:slf4j-api:1.7.25")
    implementation(project(":common"))
}
sponge {
    apiVersion("18.0.0-SNAPSHOT")
    license("MIT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("jmws") {
        displayName("JMWS")
        entrypoint("me.brynview.navidrohim.sponge.JMWSSponge")
        description("This plugin allows you to save waypoints on JourneyMap between multiple devices via the player/account UUID on the server.")
        links {
            homepage("https://spongepowered.org")
            source("https://spongepowered.org/source")
            issues("https://spongepowered.org/issues")
        }
        contributor("Navid Rohim") {
            description("Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("${rootProject.name}-sponge")
    archiveClassifier.set("")
}

val javaTarget = 21
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
    if (JavaVersion.current() < JavaVersion.toVersion(javaTarget)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaTarget))
    }
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8" // Consistent source file encoding
        release.set(javaTarget)
    }
}

// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

// Optional: configure publication to Ore
// Publish using the publishToOre task
// An API token is needed for this, by default read from the ORE_TOKEN environment variable
oreDeployment {
    // The default publication here is automatically configured by SpongeGradle
    // using the first-created plugin's ID as the project ID
    // A version body is optional, to provide additional information about the release
    /*
    defaultPublication {
        // Read the version body from the file whose path is provided to the changelog gradle property
        versionBody.set(providers.gradleProperty("changelog").map { file(it).readText(Charsets.UTF_8) }.orElse(""))
    }*/
}